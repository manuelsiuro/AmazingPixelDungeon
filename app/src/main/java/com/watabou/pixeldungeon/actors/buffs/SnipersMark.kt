package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class SnipersMark : FlavourBuff() {
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
        return BuffIndicator.MARK
    }
    override fun toString(): String {
        return "Zeroed in"
    }
    companion object {
        private const val TAG_OBJECT = "object"
    }
}
