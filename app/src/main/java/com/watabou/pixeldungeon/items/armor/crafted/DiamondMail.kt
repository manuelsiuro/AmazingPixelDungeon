package com.watabou.pixeldungeon.items.armor.crafted

import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class DiamondMail : Armor(4) {
    init {
        name = "diamond mail"
        image = ItemSpriteSheet.DIAMOND_MAIL
    }

    override fun desc(): String =
        "Mail armor studded with diamond shards that glitter in the torchlight. Nearly impervious to mundane weapons."

    override val isIdentified: Boolean
        get() = true
}
