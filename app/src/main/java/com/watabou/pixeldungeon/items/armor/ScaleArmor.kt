package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class ScaleArmor : Armor(4) {
    init {
        name = "scale armor"
        image = ItemSpriteSheet.ARMOR_SCALE
    }
    override fun desc(): String {
        return "The metal scales sewn onto a leather vest create a flexible, yet protective armor."
    }
}
