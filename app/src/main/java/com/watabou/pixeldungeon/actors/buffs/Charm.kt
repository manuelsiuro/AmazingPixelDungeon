package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class Charm : FlavourBuff() {
    var `object` = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TAG_OBJECT, `object`)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        `object` = bundle.getInt(TAG_OBJECT)
    }
    override fun icon(): Int {
        return BuffIndicator.HEART
    }
    override fun toString(): String {
        return "Charmed"
    }
    companion object {
        private const val TAG_OBJECT = "object"
        fun durationFactor(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return r?.durationFactor() ?: 1f
        }
    }
}
