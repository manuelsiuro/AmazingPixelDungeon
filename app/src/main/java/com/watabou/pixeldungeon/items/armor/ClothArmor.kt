package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class ClothArmor : Armor(1) {
    init {
        name = "cloth armor"
        image = ItemSpriteSheet.ARMOR_CLOTH
    }
    override fun desc(): String {
        return "This lightweight armor offers basic protection."
    }
}
