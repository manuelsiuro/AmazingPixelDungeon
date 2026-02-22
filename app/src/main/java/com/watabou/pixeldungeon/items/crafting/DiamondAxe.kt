package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.AxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class DiamondAxe : CraftedAxe(4, 1.1f, 0.9f) {
    override val axeTier = AxeTier.DIAMOND

    init {
        name = "diamond axe"
        image = ItemSpriteSheet.DIAMOND_AXE_TOOL
        STR = 16
    }

    override fun min0(): Int = 8
    override fun max0(): Int = 24
    override fun maxDurability(lvl: Int): Int = 240

    override fun info(): String =
        "A magnificent axe with a diamond-edged head. Cuts through any tree with ease."

    override fun desc(): String = info()
    override fun price(): Int = 120
}
