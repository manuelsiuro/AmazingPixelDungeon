package com.watabou.pixeldungeon.items
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Ankh : Item() {
    init {
        stackable = true
        name = "Ankh"
        image = ItemSpriteSheet.ANKH
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "The ancient symbol of immortality grants an ability to return to life after death. " +
                "Upon resurrection all non-equipped items are lost."
    }
    override fun price(): Int {
        return 50 * quantity
    }
}
