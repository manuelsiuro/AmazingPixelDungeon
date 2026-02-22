package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Rope : MaterialItem() {
    init {
        name = "rope"
        image = ItemSpriteSheet.ROPE
    }

    override fun price(): Int = 3

    override fun info(): String =
        "A length of rope crafted from bark strips and plant fiber. Essential for building and various crafting recipes."

    override fun desc(): String = info()
}
