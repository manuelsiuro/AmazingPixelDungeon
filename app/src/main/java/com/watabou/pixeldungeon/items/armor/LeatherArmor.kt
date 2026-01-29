package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class LeatherArmor : Armor(2) {
    init {
        name = "leather armor"
        image = ItemSpriteSheet.ARMOR_LEATHER
    }
    override fun desc(): String {
        return "Armor made from tanned monster hide. Not as light as cloth armor but provides better protection."
    }
}
