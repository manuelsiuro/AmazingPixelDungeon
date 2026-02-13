package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Bread : Food() {
    init {
        name = "bread"
        image = ItemSpriteSheet.BREAD
        energy = 300f
        message = "The fresh bread is warm and filling."
    }

    override fun info(): String =
        "A loaf of bread, baked from wheat at a furnace. Very satisfying."

    override fun desc(): String = info()

    override fun price(): Int = 15 * quantity
}
