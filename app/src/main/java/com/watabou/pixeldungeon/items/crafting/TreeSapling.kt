package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

class TreeSapling : MaterialItem() {
    init {
        name = "tree sapling"
        image = ItemSpriteSheet.TREE_SAPLING
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

    override fun price(): Int = 5

    override fun info(): String =
        "A young tree sapling ready to be planted. Use it on a tree stump to grow a new tree."

    override fun desc(): String = info()

    companion object {
        const val AC_PLANT = "PLANT"

        private val TREE_TYPES = intArrayOf(
            Terrain.TREE_OAK, Terrain.TREE_BIRCH, Terrain.TREE_PINE,
            Terrain.TREE_MAPLE, Terrain.TREE_WILLOW, Terrain.TREE_FRUIT
        )

        private val planter = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem as? TreeSapling ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to plant.")
                    return
                }

                if (level.map[cell] != Terrain.TREE_STUMP) {
                    GLog.w("You can only plant saplings on tree stumps.")
                    return
                }

                val treeType = TREE_TYPES[Random.Int(TREE_TYPES.size)]
                Level.set(cell, treeType)
                GameScene.updateMap(cell)

                item.detach(hero.belongings.backpack)
                hero.spend(1f)
                hero.busy()
                hero.sprite?.operate(cell)

                GLog.i("You plant a sapling. A new tree takes root!")
            }

            override fun prompt(): String = "Choose a stump to plant on"
        }
    }
}
