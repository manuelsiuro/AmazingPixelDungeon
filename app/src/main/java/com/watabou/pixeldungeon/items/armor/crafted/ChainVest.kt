package com.watabou.pixeldungeon.items.armor.crafted

import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class ChainVest : Armor(2) {
    init {
        name = "chain vest"
        image = ItemSpriteSheet.CHAIN_VEST
    }

    override fun desc(): String =
        "A vest of interlocking iron rings. The links are rough-hewn but hold strong against most attacks."

    override val isIdentified: Boolean
        get() = true
}
