package com.watabou.pixeldungeon.items.rings
class RingOfShadows : Ring() {
    init {
        name = "Ring of Shadows"
    }
    override fun buff(): RingBuff {
        return Shadows()
    }
    override fun desc(): String {
        return if (isKnown())
            "Enemies will be less likely to notice you if you wear this ring. Degraded rings " +
                    "of shadows will alert enemies who might otherwise not have noticed your presence."
        else
            super.desc()
    }
    inner class Shadows : RingBuff()
}
