package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Levitation
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.utils.GLog
class PotionOfLevitation : Potion() {
    init {
        name = "Potion of Levitation"
    }
    override fun apply(hero: Hero) {
        setKnown()
        Buffs.affect(hero, Levitation::class.java, Levitation.DURATION)
        GLog.i("You float into the air!")
    }
    override fun desc(): String {
        return "Drinking this curious liquid will cause you to hover in the air, " +
                "able to drift effortlessly over traps. Flames and gases " +
                "fill the air, however, and cannot be bypassed while airborne."
    }
    override fun price(): Int {
        return if (isKnown) 35 * quantity else super.price()
    }
}
