package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Apple : Food() {
    init {
        name = "apple"
        image = ItemSpriteSheet.APPLE
        energy = 100f
        message = "Juicy and refreshing!"
    }
    override fun info(): String {
        return "A surprisingly fresh apple. Not much of a meal, but a welcome find in the dungeon."
    }
    override fun price(): Int {
        return 5 * quantity
    }
}
