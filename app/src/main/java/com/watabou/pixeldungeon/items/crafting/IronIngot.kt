package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronIngot : MaterialItem() {
    init {
        name = "iron ingot"
        image = ItemSpriteSheet.IRON_INGOT
    }

    override fun price(): Int = 10

    override fun info(): String =
        "A bar of refined iron, ready for forging into weapons and armor."

    override fun desc(): String = info()
}
