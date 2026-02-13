package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.items.crafting.MaterialItem
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Wheat : MaterialItem() {
    init {
        name = "wheat"
        image = ItemSpriteSheet.WHEAT
    }

    override fun info(): String =
        "Harvested wheat. Inedible raw, but can be baked into bread at a furnace."

    override fun desc(): String = info()

    override fun price(): Int = 4 * quantity
}
