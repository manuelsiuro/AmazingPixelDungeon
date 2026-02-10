package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class StaleRation : Food() {
    init {
        name = "stale ration"
        image = ItemSpriteSheet.STALE_RATION
        energy = 150f
        message = "That ration was a bit stale."
    }
    override fun info(): String {
        return "A ration of food that has seen better days. Dry and crumbly, but still edible."
    }
    override fun price(): Int {
        return 4 * quantity
    }
}
