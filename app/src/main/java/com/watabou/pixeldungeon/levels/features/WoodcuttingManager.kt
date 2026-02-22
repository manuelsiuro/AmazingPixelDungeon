package com.watabou.pixeldungeon.levels.features

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.Bark
import com.watabou.pixeldungeon.items.crafting.Fiber
import com.watabou.pixeldungeon.items.crafting.Log
import com.watabou.pixeldungeon.items.crafting.Resin
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.crafting.TreeSapling
import com.watabou.pixeldungeon.items.food.Apple
import com.watabou.pixeldungeon.levels.AxeTier
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.TreeHardness
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

object WoodcuttingManager {

    fun chop(level: Level, cell: Int, hero: Hero, tier: AxeTier) {
        val terrain = level.map[cell]

        // Initialize blockHP if not yet set for this cell
        if (level.blockHP.get(cell, -1) < 0) {
            val hardness = TreeHardness.forTerrain(terrain)
            if (hardness != null) {
                level.blockHP.put(cell, hardness.hp)
            } else {
                // TREE_DAMAGED or unknown — default to 1
                level.blockHP.put(cell, 1)
            }
        }

        // Apply chopping damage
        val remaining = level.blockHP.get(cell) - tier.choppingDamage
        level.blockHP.put(cell, remaining)

        if (remaining <= 0) {
            // Tree destroyed
            level.blockHP.delete(cell)

            // Drop resources based on original terrain
            val drops = getDrops(terrain, Dungeon.depth)
            for (item in drops) {
                level.drop(item, cell).sprite?.drop()
            }

            Level.set(cell, Terrain.TREE_STUMP)
            GameScene.updateMap(cell)

            // Leaf particles
            CellEmitter.center(cell).burst(Speck.factory(Speck.WOOL), 6)
            Sample.play(Assets.SND_ROCKS)

            Dungeon.observe()
        } else {
            // Tree still standing — show it's damaged
            if (terrain != Terrain.TREE_DAMAGED) {
                Level.set(cell, Terrain.TREE_DAMAGED)
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
    }

    fun getDrops(terrain: Int, depth: Int): List<Item> {
        val drops = ArrayList<Item>()

        when (terrain) {
            Terrain.TREE_OAK -> {
                // 2-3 Log + 0-1 Stick + 10% Bark
                repeat(Random.IntRange(2, 3)) { drops.add(Log()) }
                if (Random.Int(2) == 0) drops.add(Stick())
                if (Random.Float() < 0.10f) drops.add(Bark())
            }

            Terrain.TREE_BIRCH -> {
                // 1-2 Log + 0-2 Stick + 15% Bark
                repeat(Random.IntRange(1, 2)) { drops.add(Log()) }
                repeat(Random.IntRange(0, 2)) { drops.add(Stick()) }
                if (Random.Float() < 0.15f) drops.add(Bark())
            }

            Terrain.TREE_PINE -> {
                // 2-3 Log + 0-1 Stick + 25% Resin
                repeat(Random.IntRange(2, 3)) { drops.add(Log()) }
                if (Random.Int(2) == 0) drops.add(Stick())
                if (Random.Float() < 0.25f) drops.add(Resin())
            }

            Terrain.TREE_MAPLE -> {
                // 2-4 Log + 0-1 Stick + 10% Bark
                repeat(Random.IntRange(2, 4)) { drops.add(Log()) }
                if (Random.Int(2) == 0) drops.add(Stick())
                if (Random.Float() < 0.10f) drops.add(Bark())
            }

            Terrain.TREE_WILLOW -> {
                // 1-2 Log + 1-2 Stick + 20% Fiber
                repeat(Random.IntRange(1, 2)) { drops.add(Log()) }
                repeat(Random.IntRange(1, 2)) { drops.add(Stick()) }
                if (Random.Float() < 0.20f) drops.add(Fiber())
            }

            Terrain.TREE_FRUIT -> {
                // 1-2 Log + 0-1 Stick + 1-2 Apple
                repeat(Random.IntRange(1, 2)) { drops.add(Log()) }
                if (Random.Int(2) == 0) drops.add(Stick())
                repeat(Random.IntRange(1, 2)) { drops.add(Apple()) }
            }

            Terrain.TREE_STUMP -> {
                // 0-1 Stick
                if (Random.Int(2) == 0) drops.add(Stick())
            }

            Terrain.TREE_DAMAGED -> {
                // 1 Log + 0-1 Stick
                drops.add(Log())
                if (Random.Int(2) == 0) drops.add(Stick())
            }
        }

        // All trees have 5% chance to drop TreeSapling
        if (terrain != Terrain.TREE_STUMP && Random.Float() < 0.05f) {
            drops.add(TreeSapling())
        }

        return drops
    }
}
