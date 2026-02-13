package com.watabou.pixeldungeon.items.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.farming.CropManager
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog

class Hoe : Item() {

    init {
        name = "hoe"
        image = ItemSpriteSheet.HOE
        defaultAction = AC_TILL
        unique = true
        stackable = false
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_TILL)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_TILL) {
            curUser = hero
            curItem = this
            GameScene.selectCell(tiller)
        } else {
            super.execute(hero, action)
        }
    }

    override fun maxDurability(lvl: Int): Int = 80

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun info(): String =
        "A sturdy hoe for tilling soil. Use it on grass or empty ground to create farmland."

    override fun desc(): String = info()

    override fun price(): Int = 20

    companion object {
        const val AC_TILL = "TILL"
        private const val TIME_TO_TILL = 1f

        private val tiller = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) return
                val hero = curUser ?: return
                val item = curItem as? Hoe ?: return
                val level = Dungeon.level ?: return

                if (!Level.adjacent(hero.pos, cell)) {
                    GLog.w("Too far away to till.")
                    return
                }

                val terrain = level.map[cell]
                if (terrain != Terrain.GRASS && terrain != Terrain.HIGH_GRASS &&
                    terrain != Terrain.EMPTY && terrain != Terrain.EMPTY_DECO &&
                    terrain != Terrain.EMBERS
                ) {
                    GLog.w("You can't till this terrain.")
                    return
                }

                if (cell == level.entrance || cell == level.exit) {
                    GLog.w("You can't till here.")
                    return
                }

                val hydrated = CropManager.isNearWater(level, cell, 4)
                val farmTerrain = if (hydrated) Terrain.HYDRATED_FARMLAND else Terrain.FARMLAND
                Level.set(cell, farmTerrain)
                GameScene.updateMap(cell)

                // Track farmland creation time for decay
                level.farmlandTimers.put(cell, CropManager.currentTime().toInt())

                CellEmitter.get(cell).burst(Speck.factory(Speck.ROCK), 3)
                Sample.play(Assets.SND_ROCKS)
                Dungeon.observe()

                GLog.i("You till the soil.")

                item.use()
                if (item.isBroken) {
                    GLog.w("The hoe breaks!")
                    item.detach(hero.belongings.backpack)
                }

                hero.spend(TIME_TO_TILL)
                hero.busy()
                hero.sprite?.operate(cell)
            }

            override fun prompt(): String = "Choose a tile to till"
        }
    }
}
