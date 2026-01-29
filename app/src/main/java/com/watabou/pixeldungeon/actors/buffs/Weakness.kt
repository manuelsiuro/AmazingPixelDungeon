package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
class Weakness : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.WEAKNESS
    }
    override fun toString(): String {
        return "Weakened"
    }
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            val hero = target as Hero
            hero.weakened = true
            hero.belongings.discharge()
            true
        } else {
            false
        }
    }
    override fun detach() {
        super.detach()
        (target as? Hero)?.weakened = false
    }
    companion object {
        private const val DURATION = 40f
        fun duration(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}
