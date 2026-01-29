package com.watabou.pixeldungeon.items.rings
class RingOfSatiety : Ring() {
    init {
        name = "Ring of Satiety"
    }
    override fun buff(): RingBuff {
        return Satiety()
    }
    override fun desc(): String {
        return if (isKnown())
            "Wearing this ring you can go without food longer. Degraded rings of satiety will cause the opposite effect."
        else
            super.desc()
    }
    inner class Satiety : RingBuff()
}
