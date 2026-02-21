package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.WallHardness
import com.watabou.pixeldungeon.levels.features.MiningManager
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback

abstract class CraftedPickaxe(tier: Int, acu: Float, dly: Float) : MeleeWeapon(tier, acu, dly) {

    abstract val pickaxeTier: PickaxeTier

    init {
        defaultAction = AC_MINE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_MINE)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_MINE) {
            // Auto-mine: if a CRACKED_WALL is adjacent, auto-target it
            val level = Dungeon.level
            if (level != null) {
                for (offset in Level.NEIGHBOURS8) {
                    val pos = hero.pos + offset
                    if (pos >= 0 && pos < Level.LENGTH && level.map[pos] == Terrain.CRACKED_WALL) {
                        executeMine(hero, level, pos)
                        return
                    }
                }
            }

            curUser = hero
            curItem = this
            GameScene.selectCell(miner)
        } else {
            super.execute(hero, action)
        }
    }

    private fun executeMine(hero: Hero, level: Level, cell: Int) {
        val tier = pickaxeTier

        hero.spend(tier.miningTime)
        hero.busy()
        hero.sprite?.attack(cell, object : Callback {
            override fun call() {
                MiningManager.mine(level, cell, hero, tier)

                use()
                if (isBroken) {
                    GLog.w("Your %s breaks!", name())
                    detachAll(hero.belongings.backpack)
                }

                hero.onOperateComplete()
            }
        })
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    companion object {
        const val AC_MINE = "MINE"

        private val miner = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem as? CraftedPickaxe ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to mine.")
                    return
                }

                val terrain = level.map[cell]
                if (!WallHardness.isMineableWall(terrain)) {
                    GLog.w("There is nothing to mine here.")
                    return
                }

                // Check tier is sufficient for this wall type
                val hardness = WallHardness.forTerrain(terrain)
                if (hardness != null && item.pickaxeTier.ordinal < hardness.minTier.ordinal) {
                    GLog.w("Your pickaxe is not strong enough to mine this wall.")
                    return
                }

                item.executeMine(hero, level, cell)
            }

            override fun prompt(): String = "Choose a wall to mine"
        }
    }
}
