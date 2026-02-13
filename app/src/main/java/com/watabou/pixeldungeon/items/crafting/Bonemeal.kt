package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.farming.CropManager
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class Bonemeal : MaterialItem() {
    init {
        name = "bonemeal"
        image = ItemSpriteSheet.BONEMEAL
        defaultAction = AC_USE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_USE)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_USE) {
            curUser = hero
            curItem = this
            GameScene.selectCell(user)
        } else {
            super.execute(hero, action)
        }
    }

    override fun info(): String =
        "Ground bone powder. Sprinkle on a growing crop to instantly mature it."

    override fun desc(): String = info()

    override fun price(): Int = 5 * quantity

    companion object {
        const val AC_USE = "USE"

        private val user = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell) && hero.pos != cell) {
                    GLog.w("Too far away.")
                    return
                }

                val crop = level.crops.get(cell)
                if (crop == null) {
                    GLog.w("There is no crop here.")
                    return
                }

                if (crop.isMature) {
                    GLog.w("This crop is already mature.")
                    return
                }

                // Instant-mature: set plantedAt far enough in the past
                crop.plantedAt = CropManager.currentTime() - crop.cropType.growthTime.toFloat()
                crop.updateStage(CropManager.currentTime())
                GameScene.updateCrop(cell)

                CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 4)
                GLog.i("The crop matures instantly!")

                item.detach(hero.belongings.backpack)
                hero.spend(1f)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose a crop to fertilize"
        }
    }
}
