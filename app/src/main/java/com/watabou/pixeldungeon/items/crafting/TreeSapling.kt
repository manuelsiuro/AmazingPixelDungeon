package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.features.TreeGenerator
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
        "A young tree sapling ready to be planted. " +
            "Use it on grass, empty ground, or a tree stump to grow a new tree."

    override fun desc(): String = info()

    companion object {
        const val AC_PLANT = "PLANT"

        private fun canPlantOn(terrain: Int): Boolean {
            return terrain == Terrain.TREE_STUMP ||
                terrain == Terrain.GRASS ||
                terrain == Terrain.HIGH_GRASS ||
                terrain == Terrain.EMPTY
        }

        private fun treeTypeForDepth(depth: Int): Int {
            val configs = TreeGenerator.getTreeConfigs(depth)
            if (configs.isNotEmpty()) {
                return configs[Random.Int(configs.size)].terrain
            }
            // Fallback for boss levels or unknown depths: soft trees
            val softTypes = intArrayOf(
                Terrain.TREE_BIRCH, Terrain.TREE_WILLOW, Terrain.TREE_FRUIT
            )
            return softTypes[Random.Int(softTypes.size)]
        }

        private fun treeNameFor(terrain: Int): String = when (terrain) {
            Terrain.TREE_OAK -> "oak tree"
            Terrain.TREE_BIRCH -> "birch tree"
            Terrain.TREE_PINE -> "pine tree"
            Terrain.TREE_MAPLE -> "maple tree"
            Terrain.TREE_WILLOW -> "willow tree"
            Terrain.TREE_FRUIT -> "fruit tree"
            else -> "tree"
        }

        private val planter = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem as? TreeSapling ?: return
                val level = Dungeon.level ?: return

                if (hero.pos != cell && !Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to plant.")
                    return
                }

                if (!canPlantOn(level.map[cell])) {
                    GLog.w("You can't plant a sapling here. Try grass or a tree stump.")
                    return
                }

                val treeType = treeTypeForDepth(Dungeon.depth)
                Level.set(cell, treeType)
                GameScene.updateMap(cell)

                // If hero was standing on the planted cell, move to adjacent passable cell
                if (hero.pos == cell) {
                    for (offset in Level.NEIGHBOURS8) {
                        val newPos = cell + offset
                        if (newPos >= 0 && newPos < Level.LENGTH &&
                            Level.passable[newPos] && Actor.findChar(newPos) == null) {
                            hero.pos = newPos
                            hero.sprite?.place(newPos)
                            break
                        }
                    }
                }

                item.detach(hero.belongings.backpack)
                hero.spend(1f)
                hero.busy()
                hero.sprite?.operate(cell)

                Dungeon.observe()

                val treeName = treeNameFor(treeType)
                GLog.i("You plant a sapling. A %s takes root!", treeName)
            }

            override fun prompt(): String = "Choose where to plant"
        }
    }
}
