package com.watabou.pixeldungeon.items.rings
class RingOfEvasion : Ring() {
    init {
        name = "Ring of Evasion"
    }
    override fun buff(): RingBuff {
        return Evasion()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring increases your chance to dodge enemy attack."
        else
            super.desc()
    }
    inner class Evasion : RingBuff()
}
