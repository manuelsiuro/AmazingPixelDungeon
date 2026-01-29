package com.watabou.pixeldungeon.items.quest
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class DarkGold : Item() {
    init {
        name = "dark gold ore"
        image = ItemSpriteSheet.ORE
        stackable = true
        unique = true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "This metal is called dark not because of its color (it doesn't differ from the normal gold), " +
                "but because it melts under the daylight, making it useless on the surface."
    }
    override fun price(): Int {
        return quantity
    }
}
