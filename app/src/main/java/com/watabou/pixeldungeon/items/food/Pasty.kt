package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Pasty : Food() {
    init {
        name = "pasty"
        image = ItemSpriteSheet.PASTY
        energy = Hunger.STARVING
    }
    override fun info(): String {
        return "This is authentic Cornish pasty with traditional filling of beef and potato."
    }
    override fun price(): Int {
        return 20 * quantity
    }
}
