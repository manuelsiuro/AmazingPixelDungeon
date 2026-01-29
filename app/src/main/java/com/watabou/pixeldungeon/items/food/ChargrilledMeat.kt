package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class ChargrilledMeat : Food() {
    init {
        name = "chargrilled meat"
        image = ItemSpriteSheet.STEAK
        energy = Hunger.STARVING - Hunger.HUNGRY
    }
    override fun info(): String {
        return "It looks like a decent steak."
    }
    override fun price(): Int {
        return 5 * quantity
    }
    companion object {
        fun cook(ingredient: MysteryMeat): Food {
            val result = ChargrilledMeat()
            result.quantity = ingredient.quantity
            return result
        }
    }
}
