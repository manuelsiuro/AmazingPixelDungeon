package com.watabou.pixeldungeon.items.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.building.PlacementValidator
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class SupportBeamItem : MaterialItem() {
    init {
        name = "support beam"
        image = ItemSpriteSheet.SUPPORT_BEAM_ITEM
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

    override fun price(): Int = 10

    override fun info(): String =
        "A sturdy wooden support beam. Place it to create a solid pillar " +
        "that blocks movement and line of sight."

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

                val error = PlacementValidator.validate(cell, hero)
                if (error != null) {
                    GLog.w(error)
                    return
                }

                Level.set(cell, Terrain.SUPPORT_BEAM)
                GameScene.updateMap(cell)
                level.buildCount++

                CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 4)
                Sample.play(Assets.SND_ROCKS)
                Dungeon.observe()

                item.detach(hero.belongings.backpack)
                hero.spend(TIME_TO_PLACE)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose a tile to place the support beam"
        }
    }
}
