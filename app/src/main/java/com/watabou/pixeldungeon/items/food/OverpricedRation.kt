package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class OverpricedRation : Food() {
    init {
        name = "overpriced food ration"
        image = ItemSpriteSheet.OVERPRICED
        energy = Hunger.STARVING - Hunger.HUNGRY
        message = "That food tasted ok."
    }
    override fun info(): String {
        return "It looks exactly like a standard ration of food but smaller."
    }
    override fun price(): Int {
        return 20 * quantity
    }
}
