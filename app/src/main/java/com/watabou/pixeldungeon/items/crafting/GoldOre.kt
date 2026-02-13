package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class GoldOre : MaterialItem() {
    init {
        name = "gold ore"
        image = ItemSpriteSheet.GOLD_ORE
    }

    override fun price(): Int = 8

    override fun info(): String =
        "A glittering chunk of gold-bearing rock. Can be refined into a pure gold ingot."

    override fun desc(): String = info()
}
