package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodenClub : MeleeWeapon(1, 0.8f, 1.2f) {
    init {
        name = "wooden club"
        image = ItemSpriteSheet.WOODEN_CLUB
        STR = 10
    }

    override fun desc(): String =
        "A crude club carved from a thick branch. It's heavy and unwieldy, but effective enough to crack a skull."

    override val isIdentified: Boolean
        get() = true
}
