package com.watabou.pixeldungeon.items.food.farming

import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Carrot : Food() {
    init {
        name = "carrot"
        image = ItemSpriteSheet.CARROT
        energy = 50f
        message = "The carrot is crunchy and sweet."
    }

    override fun info(): String =
        "A fresh carrot, pulled straight from the soil. Edible raw."

    override fun desc(): String = info()

    override fun price(): Int = 6 * quantity
}
