package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.AxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodenAxe : CraftedAxe(1, 1f, 1.1f) {
    override val axeTier = AxeTier.WOOD

    init {
        name = "wooden axe"
        image = ItemSpriteSheet.WOODEN_AXE
        STR = 10
    }

    override fun min0(): Int = 2
    override fun max0(): Int = 8
    override fun maxDurability(lvl: Int): Int = 30

    override fun info(): String =
        "A crude axe with a wooden head. Only suitable for chopping soft trees like birch and willow."

    override fun desc(): String = info()
    override fun price(): Int = 15
}
