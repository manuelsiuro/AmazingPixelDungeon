package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodenBowl : MaterialItem() {
    init {
        name = "wooden bowl"
        image = ItemSpriteSheet.WOODEN_BOWL
    }

    override fun info(): String =
        "A simple wooden bowl, carved from planks. Used as an ingredient for stew."

    override fun desc(): String = info()

    override fun price(): Int = 3 * quantity
}
