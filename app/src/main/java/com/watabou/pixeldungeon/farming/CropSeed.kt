package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.utils.GLog
import java.util.ArrayList

abstract class CropSeed : Item() {

    abstract val cropType: CropType

    init {
        stackable = true
        defaultAction = AC_PLANT
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_PLANT)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_PLANT) {
            curUser = hero
            curItem = this
            GameScene.selectCell(planter)
        } else {
            super.execute(hero, action)
        }
    }

    override fun onThrow(cell: Int) {
        val level = Dungeon.level ?: return
        val terrain = level.map[cell]
        if ((terrain == Terrain.FARMLAND || terrain == Terrain.HYDRATED_FARMLAND) &&
            level.crops.get(cell) == null
        ) {
            CropManager.plantCrop(level, cell, cropType)
            GLog.i("The %s takes root in the soil.", name)
        } else {
            super.onThrow(cell)
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun price(): Int = 5 * quantity

    companion object {
        const val AC_PLANT = "PLANT"
        private const val TIME_TO_PLANT = 1f

        private val planter = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell) && hero.pos != cell) {
                    GLog.w("Too far away to plant.")
                    return
                }

                val terrain = level.map[cell]
                if (terrain != Terrain.FARMLAND && terrain != Terrain.HYDRATED_FARMLAND) {
                    GLog.w("You can only plant on tilled soil.")
                    return
                }

                if (level.crops.get(cell) != null) {
                    GLog.w("Something is already growing here.")
                    return
                }

                val seed = item as CropSeed
                CropManager.plantCrop(level, cell, seed.cropType)
                GLog.i("You plant the %s.", item.name)

                item.detach(hero.belongings.backpack)
                hero.spend(TIME_TO_PLANT)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose farmland to plant the seed"
        }
    }
}
