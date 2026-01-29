package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class Terror : FlavourBuff() {
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
        return BuffIndicator.TERROR
    }
    override fun toString(): String {
        return "Terror"
    }
    companion object {
        const val DURATION = 10f
        private const val TAG_OBJECT = "object"
        fun recover(target: Char) {
            val terror = target.buff(Terror::class.java)
            if (terror != null && terror.cooldown() < DURATION) {
                target.remove(terror)
            }
        }
    }
}
