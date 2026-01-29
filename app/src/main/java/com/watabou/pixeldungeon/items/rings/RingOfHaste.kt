package com.watabou.pixeldungeon.items.rings
class RingOfHaste : Ring() {
    init {
        name = "Ring of Haste"
    }
    override fun buff(): RingBuff {
        return Haste()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring accelerates the wearer's flow of time, allowing one to perform all actions a little faster."
        else
            super.desc()
    }
    inner class Haste : RingBuff()
}
