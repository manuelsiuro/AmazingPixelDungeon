package com.watabou.pixeldungeon.items.rings
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
class RingOfThorns : Ring() {
    init {
        name = "Ring of Thorns"
    }
    override fun buff(): RingBuff {
        return Thorns()
    }
    override fun random(): Item {
        level(+1)
        return this
    }
    override fun doPickUp(hero: Hero): Boolean {
        identify()
        Badges.validateRingOfThorns()
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
            "Though this ring doesn't provide real thorns, an enemy that attacks you " +
                    "will itself be wounded by a fraction of the damage that it inflicts. " +
                    "Upgrading this ring won't give any additional bonuses."
        else
            super.desc()
    }
    inner class Thorns : RingBuff()
}
