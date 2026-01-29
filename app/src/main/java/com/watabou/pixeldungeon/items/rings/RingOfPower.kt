package com.watabou.pixeldungeon.items.rings
class RingOfPower : Ring() {
    init {
        name = "Ring of Power"
    }
    override fun buff(): RingBuff {
        return Power()
    }
    override fun desc(): String {
        return if (isKnown())
            "Your wands will become more powerful in the energy field " +
                    "that radiates from this ring. Degraded rings of power will instead weaken your wands."
        else
            super.desc()
    }
    inner class Power : RingBuff()
}
