package com.watabou.pixeldungeon.items

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Skeleton
import com.watabou.pixeldungeon.actors.mobs.Wraith
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.ArrayList

class HolyWater : Item() {

    init {
        name = "holy water"
        image = ItemSpriteSheet.HOLY_WATER
        defaultAction = AC_THROW
        stackable = true
    }

    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_DRINK)
        return actions
    }

    override fun execute(hero: Hero, action: String) {
        if (action == AC_DRINK) {
            detach(hero.belongings.backpack)
            // Remove curse from one random equipped item
            val equipped = ArrayList<Item>()
            hero.belongings.weapon?.let { if (it.cursed) equipped.add(it) }
            hero.belongings.armor?.let { if (it.cursed) equipped.add(it) }
            hero.belongings.ring1?.let { if (it.cursed) equipped.add(it) }
            hero.belongings.ring2?.let { if (it.cursed) equipped.add(it) }

            if (equipped.isNotEmpty()) {
                val item = equipped[Random.Int(equipped.size)]
                item.cursed = false
                GLog.p("The holy water purifies your %s!", item.name())
            } else {
                GLog.i("The holy water washes over you, but there is nothing to purify.")
            }
            hero.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.2f, 3)
            Sample.play(Assets.SND_DRINK)
            hero.spendAndNext(1f)
        } else {
            super.execute(hero, action)
        }
    }

    override fun onThrow(cell: Int) {
        if (Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            val ch = Actor.findChar(cell)
            if (ch != null) {
                // Bonus damage to undead
                if (ch is Skeleton || ch is Wraith) {
                    val dmg = Random.IntRange(10, 20 + Dungeon.depth)
                    ch.damage(dmg, this)
                    ch.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.2f, 3)
                } else {
                    ch.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.2f, 1)
                }
            }
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun random(): Item {
        quantity = Random.IntRange(1, 2)
        return this
    }

    override fun price(): Int {
        return 20 * quantity
    }

    override fun info(): String {
        return "A vial of blessed water from a forgotten temple. When thrown, it sears undead creatures. " +
                "Drinking it will remove a curse from one of your equipped items."
    }

    companion object {
        private const val AC_DRINK = "DRINK"
    }
}
