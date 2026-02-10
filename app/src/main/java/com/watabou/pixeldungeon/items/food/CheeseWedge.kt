package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class CheeseWedge : Food() {
    init {
        name = "cheese wedge"
        image = ItemSpriteSheet.CHEESE_WEDGE
        energy = 200f
        message = "Sharp and flavorful!"
    }
    override fun info(): String {
        return "A wedge of pungent cave-aged cheese. Rich and filling."
    }
    override fun price(): Int {
        return 12 * quantity
    }
}
