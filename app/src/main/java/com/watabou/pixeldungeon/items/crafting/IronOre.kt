package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronOre : MaterialItem() {
    init {
        name = "iron ore"
        image = ItemSpriteSheet.IRON_ORE
    }

    override fun price(): Int = 5

    override fun info(): String =
        "A chunk of raw iron ore mined from the dungeon depths. Can be smelted into iron ingots."

    override fun desc(): String = info()
}
