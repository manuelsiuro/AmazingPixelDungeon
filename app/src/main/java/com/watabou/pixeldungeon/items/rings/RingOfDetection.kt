package com.watabou.pixeldungeon.items.rings
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
class RingOfDetection : Ring() {
    init {
        name = "Ring of Detection"
    }
    override fun doEquip(hero: Hero): Boolean {
        if (super.doEquip(hero)) {
            Dungeon.hero?.search(false)
            return true
        } else {
            return false
        }
    }
    override fun buff(): RingBuff {
        return Detection()
    }
    override fun desc(): String {
        return if (isKnown())
            "Wearing this ring will allow the wearer to notice hidden secrets - " +
                    "traps and secret doors - without taking time to search. Degraded rings of detection " +
                    "will dull your senses, making it harder to notice secrets even when actively searching for them."
        else
            super.desc()
    }
    inner class Detection : RingBuff()
}
