package com.watabou.pixeldungeon.items.armor.crafted

import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class LeatherTunic : Armor(1) {
    init {
        name = "leather tunic"
        image = ItemSpriteSheet.LEATHER_TUNIC
    }

    override fun desc(): String =
        "A tunic stitched together from scraps of dungeon leather. It offers modest protection against claws and blades."

    override val isIdentified: Boolean
        get() = true
}
