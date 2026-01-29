package com.watabou.pixeldungeon.items.rings
class RingOfAccuracy : Ring() {
    init {
        name = "Ring of Accuracy"
    }
    override fun buff(): RingBuff {
        return Accuracy()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring increases your chance to hit the enemy."
        else
            super.desc()
    }
    inner class Accuracy : RingBuff()
}
