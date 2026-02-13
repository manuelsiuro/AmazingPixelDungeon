package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Stick : MaterialItem() {
    init {
        name = "stick"
        image = ItemSpriteSheet.STICK
    }

    override fun price(): Int = 1

    override fun info(): String =
        "A sturdy wooden stick. A basic but versatile crafting component."

    override fun desc(): String = info()
}
