package com.watabou.pixeldungeon.items.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class WaterBucket : MaterialItem() {
    init {
        name = "water bucket"
        image = ItemSpriteSheet.WATER_BUCKET
        stackable = false
        defaultAction = AC_POUR
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_POUR)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_POUR) {
            curUser = hero
            curItem = this
            GameScene.selectCell(pourer)
        } else {
            super.execute(hero, action)
        }
    }

    override fun info(): String =
        "A bucket of water. Pour it on a tile to create a water puddle for irrigating crops."

    override fun desc(): String = info()

    override fun price(): Int = 15

    companion object {
        const val AC_POUR = "POUR"

        private val pourer = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away.")
                    return
                }

                val terrain = level.map[cell]
                if (terrain != Terrain.EMPTY && terrain != Terrain.GRASS &&
                    terrain != Terrain.EMBERS && terrain != Terrain.EMPTY_SP &&
                    terrain != Terrain.EMPTY_DECO && terrain != Terrain.FARMLAND &&
                    terrain != Terrain.HYDRATED_FARMLAND
                ) {
                    GLog.w("You can't pour water here.")
                    return
                }

                if (Actor.findChar(cell) != null) {
                    GLog.w("Something is in the way.")
                    return
                }

                if (terrain == Terrain.FARMLAND) {
                    Level.set(cell, Terrain.HYDRATED_FARMLAND)
                    GameScene.updateMap(cell)
                    GLog.i("You water the farmland.")
                } else if (terrain == Terrain.HYDRATED_FARMLAND) {
                    GLog.i("This farmland is already hydrated.")
                    return
                } else {
                    Level.set(cell, Terrain.WATER)
                    GameScene.updateMap(cell)
                    GLog.i("You pour water onto the ground.")
                }

                CellEmitter.get(cell).burst(Speck.factory(Speck.STEAM), 4)
                Sample.play(Assets.SND_WATER)
                Dungeon.observe()

                item.detach(hero.belongings.backpack)
                hero.spend(1f)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose a tile to pour water"
        }
    }
}
