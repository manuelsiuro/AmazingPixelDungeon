package com.watabou.pixeldungeon.items.rings
class RingOfMending : Ring() {
    init {
        name = "Ring of Mending"
    }
    override fun buff(): RingBuff {
        return Rejuvenation()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring increases the body's regenerative properties, allowing " +
                    "one to recover lost health at an accelerated rate. Degraded rings will " +
                    "decrease or even halt one's natural regeneration."
        else
            super.desc()
    }
    inner class Rejuvenation : RingBuff()
}
