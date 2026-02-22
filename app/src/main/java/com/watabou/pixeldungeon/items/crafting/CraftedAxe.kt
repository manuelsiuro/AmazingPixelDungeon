package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.levels.AxeTier
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.TreeHardness
import com.watabou.pixeldungeon.levels.features.WoodcuttingManager
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Callback

abstract class CraftedAxe(tier: Int, acu: Float, dly: Float) : MeleeWeapon(tier, acu, dly) {

    abstract val axeTier: AxeTier

    init {
        defaultAction = AC_CHOP
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_CHOP)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_CHOP) {
            // Auto-chop: if a TREE_DAMAGED is adjacent, auto-target it
            val level = Dungeon.level
            if (level != null) {
                for (offset in Level.NEIGHBOURS8) {
                    val pos = hero.pos + offset
                    if (pos >= 0 && pos < Level.LENGTH && level.map[pos] == Terrain.TREE_DAMAGED) {
                        executeChop(hero, level, pos)
                        return
                    }
                }
            }

            curUser = hero
            curItem = this
            GameScene.selectCell(chopper)
        } else {
            super.execute(hero, action)
        }
    }

    private fun executeChop(hero: Hero, level: Level, cell: Int) {
        val tier = axeTier

        hero.spend(tier.choppingTime)
        hero.busy()
        hero.sprite?.attack(cell, object : Callback {
            override fun call() {
                WoodcuttingManager.chop(level, cell, hero, tier)

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
        const val AC_CHOP = "CHOP"

        private val chopper = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem as? CraftedAxe ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to chop.")
                    return
                }

                val terrain = level.map[cell]
                if (!Terrain.isChoppable(terrain)) {
                    GLog.w("There is nothing to chop here.")
                    return
                }

                // Check tier is sufficient for this tree type
                val hardness = TreeHardness.forTerrain(terrain)
                if (hardness != null && item.axeTier.ordinal < hardness.minTier.ordinal) {
                    GLog.w("Your axe is not strong enough to chop this tree.")
                    return
                }

                item.executeChop(hero, level, cell)
            }

            override fun prompt(): String = "Choose a tree to chop"
        }
    }
}
