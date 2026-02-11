package com.watabou.pixeldungeon.items.potions

import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Speed
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.utils.GLog

class PotionOfSpeed : Potion() {

    init {
        name = "Potion of Speed"
    }

    override fun apply(hero: Hero) {
        setKnown()
        Buffs.prolong(hero, Speed::class.java, 10f)
        hero.sprite?.emitter()?.start(Speck.factory(Speck.JET), 0.2f, 10)
        GLog.i("You feel the world slow down around you!")
    }

    override fun desc(): String {
        return "Drinking this potion will grant incredible swiftness for a short time, " +
                "allowing you to take actions in half the normal time."
    }

    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
}
