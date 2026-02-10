package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class SmokeyBacon : Food() {
    init {
        name = "smokey bacon"
        image = ItemSpriteSheet.SMOKEY_BACON
        energy = 180f
        message = "Crispy and delicious!"
    }
    override fun info(): String {
        return "Thick-cut bacon, slowly smoked over a dungeon fire. The salty flavor is irresistible."
    }
    override fun price(): Int {
        return 8 * quantity
    }
}
