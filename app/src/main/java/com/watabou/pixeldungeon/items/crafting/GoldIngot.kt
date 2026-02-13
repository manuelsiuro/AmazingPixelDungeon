package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class GoldIngot : MaterialItem() {
    init {
        name = "gold ingot"
        image = ItemSpriteSheet.GOLD_INGOT
    }

    override fun price(): Int = 15

    override fun info(): String =
        "A bar of pure gold, dense and precious. Used in crafting the finest equipment."

    override fun desc(): String = info()
}
