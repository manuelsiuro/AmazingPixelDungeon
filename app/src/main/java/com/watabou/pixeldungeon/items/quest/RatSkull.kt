package com.watabou.pixeldungeon.items.quest
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class RatSkull : Item() {
    init {
        name = "giant rat skull"
        image = ItemSpriteSheet.SKULL
        unique = true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "It could be a nice hunting trophy, but it smells too bad to place it on a wall."
    }
    override fun price(): Int {
        return 100
    }
}
