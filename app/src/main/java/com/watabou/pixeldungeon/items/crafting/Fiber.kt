package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Fiber : MaterialItem() {
    init {
        name = "plant fiber"
        image = ItemSpriteSheet.FIBER
    }

    override fun price(): Int = 1

    override fun info(): String =
        "Tough fibers stripped from dungeon plants. Can be woven into crude bindings or used as crafting material."

    override fun desc(): String = info()
}
