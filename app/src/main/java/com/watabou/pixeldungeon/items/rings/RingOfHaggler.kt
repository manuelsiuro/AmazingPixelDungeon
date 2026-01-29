package com.watabou.pixeldungeon.items.rings
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
class RingOfHaggler : Ring() {
    init {
        name = "Ring of Haggler"
    }
    override fun buff(): RingBuff {
        return Haggling()
    }
    override fun random(): Item {
        level(+1)
        return this
    }
    override fun doPickUp(hero: Hero): Boolean {
        identify()
        Badges.validateRingOfHaggler()
        Badges.validateItemLevelAquired(this)
        return super.doPickUp(hero)
    }
    override val isUpgradable: Boolean
        get() = false
    override fun use() {
        // Do nothing (it can't degrade)
    }
    override fun desc(): String {
        return if (isKnown())
            "In fact this ring doesn't provide any magic effect, but it demonstrates " +
                    "to shopkeepers and vendors, that the owner of the ring is a member of " +
                    "The Thieves' Guild. Usually they are glad to give a discount in exchange " +
                    "for temporary immunity guarantee. Upgrading this ring won't give any additional " +
                    "bonuses."
        else
            super.desc()
    }
    inner class Haggling : RingBuff()
}
