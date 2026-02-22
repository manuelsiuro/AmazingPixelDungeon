package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodenFence : MaterialItem() {
    init {
        name = "wooden fence"
        image = ItemSpriteSheet.WOODEN_FENCE
    }

    override fun price(): Int = 4

    override fun info(): String =
        "A placeable wooden fence section, constructed from logs and rope. Can be used to block passages and corral enemies."

    override fun desc(): String = info()
}
