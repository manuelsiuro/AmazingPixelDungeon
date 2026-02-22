package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Resin : MaterialItem() {
    init {
        name = "resin"
        image = ItemSpriteSheet.RESIN
    }

    override fun price(): Int = 2

    override fun info(): String =
        "Sticky amber resin harvested from pine trees. It can be used as an adhesive or fuel in crafting."

    override fun desc(): String = info()
}
