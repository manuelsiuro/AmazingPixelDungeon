package com.watabou.pixeldungeon.items.rings
class RingOfHerbalism : Ring() {
    init {
        name = "Ring of Herbalism"
    }
    override fun buff(): RingBuff {
        return Herbalism()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring increases your chance to gather dew and seeds from trampled grass."
        else
            super.desc()
    }
    inner class Herbalism : RingBuff()
}
