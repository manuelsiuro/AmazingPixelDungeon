package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.AxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronAxe : CraftedAxe(3, 1f, 1f) {
    override val axeTier = AxeTier.IRON

    init {
        name = "iron axe"
        image = ItemSpriteSheet.IRON_AXE_TOOL
        STR = 14
    }

    override fun min0(): Int = 6
    override fun max0(): Int = 18
    override fun maxDurability(lvl: Int): Int = 120

    override fun info(): String =
        "A well-forged iron axe. Strong enough to fell even the hardest pine trees."

    override fun desc(): String = info()
    override fun price(): Int = 45
}
