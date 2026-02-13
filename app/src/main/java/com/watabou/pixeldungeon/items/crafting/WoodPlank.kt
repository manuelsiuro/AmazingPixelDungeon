package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodPlank : MaterialItem() {
    init {
        name = "wood plank"
        image = ItemSpriteSheet.WOOD_PLANK
    }

    override fun price(): Int = 3

    override fun info(): String =
        "A flat piece of wood, shaped for building. Essential for constructing barricades and wooden equipment."

    override fun desc(): String = info()
}
