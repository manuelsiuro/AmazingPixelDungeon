package com.watabou.pixeldungeon.levels.features

import com.watabou.noosa.Camera
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.ArcaneOre
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.DiamondShard
import com.watabou.pixeldungeon.items.crafting.GoldOre
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.WallHardness
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

object MiningManager {

    fun mine(level: Level, cell: Int, hero: Hero, tier: PickaxeTier) {
        val terrain = level.map[cell]

        // Initialize blockHP if not yet set for this cell
        if (level.blockHP.get(cell, -1) < 0) {
            val hardness = WallHardness.forTerrain(terrain)
            if (hardness != null) {
                level.blockHP.put(cell, hardness.hp)
            } else {
                // CRACKED_WALL or unknown — default to 1
                level.blockHP.put(cell, 1)
            }
        }

        // Apply mining damage
        val remaining = level.blockHP.get(cell) - tier.miningDamage
        level.blockHP.put(cell, remaining)

        if (remaining <= 0) {
            // Wall destroyed
            level.blockHP.delete(cell)

            // Drop resources based on original terrain
            val drops = getDrops(terrain, Dungeon.depth)
            for (item in drops) {
                level.drop(item, cell).sprite?.drop()
            }

            Level.set(cell, Terrain.EMPTY)
            GameScene.updateMap(cell)

            CellEmitter.center(cell).burst(Speck.factory(Speck.STAR), 7)
            Sample.play(Assets.SND_ROCKS)

            checkCaveIn(level, cell, hero)

            Dungeon.observe()
        } else {
            // Wall still standing — show it's cracked
            if (terrain != Terrain.CRACKED_WALL) {
                Level.set(cell, Terrain.CRACKED_WALL)
                // Preserve the remaining HP
                GameScene.updateMap(cell)
            }

            CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 3)
            Sample.play(Assets.SND_ROCKS)
        }

        // Apply hunger cost
        val hunger = hero.buff(Hunger::class.java)
        if (hunger != null && !hunger.isStarving) {
            hunger.satisfy(-Hunger.STARVING / tier.hungerDivisor)
            BuffIndicator.refreshHero()
        }

        // Emit mining noise to attract nearby mobs
        MiningNoise.emit(cell, terrain)
    }

    fun getDrops(terrain: Int, depth: Int): List<Item> {
        val drops = ArrayList<Item>()

        when (terrain) {
            Terrain.DIRT_WALL, Terrain.STONE_WALL_NATURAL,
            Terrain.GRANITE_WALL, Terrain.OBSIDIAN_WALL -> {
                // Geological walls: 1-3 cobblestone
                val n = Random.IntRange(1, 3)
                for (i in 0 until n) {
                    drops.add(Cobblestone())
                }
                // 15% chance of depth-appropriate ore
                if (Random.Float() < 0.15f) {
                    when {
                        depth >= 16 -> drops.add(DiamondShard())
                        depth >= 11 -> drops.add(GoldOre())
                        depth >= 6 -> drops.add(IronOre())
                        else -> drops.add(Cobblestone())
                    }
                }
            }

            Terrain.ORE_WALL_IRON -> {
                // 1-2 iron ore + 0-1 cobblestone
                val n = Random.IntRange(1, 2)
                for (i in 0 until n) {
                    drops.add(IronOre())
                }
                if (Random.Int(2) == 0) {
                    drops.add(Cobblestone())
                }
            }

            Terrain.ORE_WALL_GOLD -> {
                // 1 gold ore + 0-1 cobblestone
                drops.add(GoldOre())
                if (Random.Int(2) == 0) {
                    drops.add(Cobblestone())
                }
            }

            Terrain.ORE_WALL_DIAMOND -> {
                // 1-2 diamond shards + 0-1 cobblestone
                val n = Random.IntRange(1, 2)
                for (i in 0 until n) {
                    drops.add(DiamondShard())
                }
                if (Random.Int(2) == 0) {
                    drops.add(Cobblestone())
                }
            }

            Terrain.ORE_WALL_ARCANE -> {
                // 1 arcane ore + 0-1 cobblestone
                drops.add(ArcaneOre())
                if (Random.Int(2) == 0) {
                    drops.add(Cobblestone())
                }
            }

            Terrain.CRACKED_WALL -> {
                // Cracked walls just drop 1-2 cobblestone
                val n = Random.IntRange(1, 2)
                for (i in 0 until n) {
                    drops.add(Cobblestone())
                }
            }
        }

        return drops
    }

    fun checkCaveIn(level: Level, cell: Int, hero: Hero) {
        // Count solid walls in 8 neighbors
        var solidCount = 0
        var hasSupport = false

        // Check 3x3 area for support beams
        for (offset in Level.NEIGHBOURS9) {
            val n = cell + offset
            if (n >= 0 && n < Level.LENGTH) {
                if (level.map[n] == Terrain.SUPPORT_BEAM) {
                    hasSupport = true
                    break
                }
            }
        }

        if (hasSupport) return

        for (offset in Level.NEIGHBOURS8) {
            val n = cell + offset
            if (n >= 0 && n < Level.LENGTH && Level.solid[n]) {
                solidCount++
            }
        }

        // Cave-in chances based on surrounding support
        val caveInChance = when {
            solidCount >= 5 -> 0f
            solidCount == 4 -> 0.05f
            solidCount == 3 -> 0.15f
            solidCount == 2 -> 0.30f
            else -> 0.50f
        }

        if (Random.Float() >= caveInChance) return

        // Cave-in occurs!
        GLog.n("The ceiling caves in!")

        // 1-3 adjacent passable cells become rubble
        val rubbleCount = Random.IntRange(1, 3)
        val candidates = ArrayList<Int>()
        for (offset in Level.NEIGHBOURS8) {
            val n = cell + offset
            if (n >= 0 && n < Level.LENGTH && Level.passable[n]) {
                candidates.add(n)
            }
        }
        candidates.shuffle()
        for (i in 0 until minOf(rubbleCount, candidates.size)) {
            val rubbleCell = candidates[i]
            Level.set(rubbleCell, Terrain.RUBBLE)
            GameScene.updateMap(rubbleCell)
            CellEmitter.get(rubbleCell).burst(Speck.factory(Speck.ROCK), 4)
        }

        // Hero takes damage: 5 + depth/3
        val damage = 5 + Dungeon.depth / 3
        hero.damage(damage, this)

        // Apply Paralysis for 1-2 turns and Blindness for 2 turns
        Buffs.prolong(hero, Paralysis::class.java, Random.IntRange(1, 2).toFloat())
        Buffs.prolong(hero, Blindness::class.java, 2f)

        // Camera shake
        Camera.main?.shake(3f, 0.7f)
        Sample.play(Assets.SND_ROCKS)

        MiningNoise.emitCaveIn(cell)
    }
}
