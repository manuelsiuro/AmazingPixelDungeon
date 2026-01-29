package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
class Paralysis : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            target.paralysed = true
            true
        } else {
            false
        }
    }
    override fun detach() {
        val target = target
        super.detach()
        target?.let { unfreeze(it) }
    }
    override fun icon(): Int {
        return BuffIndicator.PARALYSIS
    }
    override fun toString(): String {
        return "Paralysed"
    }
    companion object {
        private const val DURATION = 10f
        fun duration(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
        fun unfreeze(ch: Char) {
            if (ch.buff(Paralysis::class.java) == null &&
                ch.buff(Frost::class.java) == null
            ) {
                ch.paralysed = false
            }
        }
    }
}
