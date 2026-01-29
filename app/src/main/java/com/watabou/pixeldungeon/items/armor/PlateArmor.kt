package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class PlateArmor : Armor(5) {
    init {
        name = "plate armor"
        image = ItemSpriteSheet.ARMOR_PLATE
    }
    override fun desc(): String {
        return "Enormous plates of metal are joined together into a suit that provides " +
                "unmatched protection to any adventurer strong enough to bear its staggering weight."
    }
}
