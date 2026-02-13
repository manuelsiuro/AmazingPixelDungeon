package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Cobblestone : MaterialItem() {
    init {
        name = "cobblestone"
        image = ItemSpriteSheet.COBBLESTONE
    }

    override fun price(): Int = 2

    override fun info(): String =
        "A rough chunk of stone, broken from dungeon walls. Useful for crude construction and crafting."

    override fun desc(): String = info()
}
