package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Leather : MaterialItem() {
    init {
        name = "leather"
        image = ItemSpriteSheet.LEATHER
    }

    override fun price(): Int = 5

    override fun info(): String =
        "A piece of cured leather, tough and flexible. Ideal for crafting protective gear."

    override fun desc(): String = info()
}
