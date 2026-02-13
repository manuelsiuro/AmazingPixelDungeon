package com.watabou.pixeldungeon.items.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.DimensionalChestNpc
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class DimensionalChestItem : MaterialItem() {
    init {
        name = "dimensional chest"
        image = ItemSpriteSheet.DIMENSIONAL_CHEST
        stackable = false
        unique = true
        defaultAction = AC_PLACE
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_PLACE)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_PLACE) {
            curUser = hero
            curItem = this
            GameScene.selectCell(placer)
        } else {
            super.execute(hero, action)
        }
    }

    override fun price(): Int = 50

    override fun info(): String =
        "A chest linked to a pocket dimension. Items stored inside can be accessed " +
        "from any other dimensional chest, regardless of depth."

    override fun desc(): String = info()

    companion object {
        const val AC_PLACE = "PLACE"
        private const val TIME_TO_PLACE = 1f

        private val placer = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("You can only place chests on adjacent tiles.")
                    return
                }
                val terrain = level.map[cell]
                if (terrain != Terrain.EMPTY && terrain != Terrain.GRASS &&
                    terrain != Terrain.EMBERS && terrain != Terrain.EMPTY_SP &&
                    terrain != Terrain.EMPTY_DECO
                ) {
                    GLog.w("You can't place a chest here.")
                    return
                }
                if (Actor.findChar(cell) != null) {
                    GLog.w("Something is in the way.")
                    return
                }
                if (level.heaps[cell] != null) {
                    GLog.w("There are items in the way.")
                    return
                }
                if (Dungeon.bossLevel()) {
                    GLog.w("The dungeon resists your construction.")
                    return
                }

                val chest = DimensionalChestNpc()
                chest.pos = cell
                GameScene.add(chest)

                CellEmitter.get(cell).burst(Speck.factory(Speck.WOOL), 4)
                Sample.play(Assets.SND_ROCKS)
                Dungeon.observe()

                item.detach(hero.belongings.backpack)
                hero.spend(TIME_TO_PLACE)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose a tile to place the dimensional chest"
        }
    }
}
