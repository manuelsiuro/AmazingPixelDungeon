package com.watabou.pixeldungeon.items.potions

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Barkskin
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.utils.GLog

class PotionOfShielding : Potion() {

    init {
        name = "Potion of Shielding"
    }

    override fun apply(hero: Hero) {
        setKnown()
        val shieldLevel = 10 + Dungeon.depth
        Buffs.affect(hero, Barkskin::class.java)?.level(shieldLevel)
        hero.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.2f, 5)
        GLog.i("A protective shield forms around you!")
    }

    override fun desc(): String {
        return "This potion envelops the drinker in a magical shield that absorbs damage. " +
                "The shield's strength scales with the depth of the dungeon."
    }

    override fun price(): Int {
        return if (isKnown) 35 * quantity else super.price()
    }
}
