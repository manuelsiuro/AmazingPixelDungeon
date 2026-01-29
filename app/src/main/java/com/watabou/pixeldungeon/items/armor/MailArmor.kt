package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class MailArmor : Armor(3) {
    init {
        name = "mail armor"
        image = ItemSpriteSheet.ARMOR_MAIL
    }
    override fun desc(): String {
        return "Interlocking metal links make for a tough but flexible suit of armor."
    }
}
