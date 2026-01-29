package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
class Vertigo : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.VERTIGO
    }
    override fun toString(): String {
        return "Vertigo"
    }
    companion object {
        const val DURATION = 10f
        fun duration(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}
