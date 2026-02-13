package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronMace : MeleeWeapon(3, 0.8f, 1.2f) {
    init {
        name = "iron mace"
        image = ItemSpriteSheet.CRAFTED_IRON_MACE
        STR = 16
    }

    override fun desc(): String =
        "A heavy iron mace with a flanged head. What it lacks in speed, it makes up for in crushing power."

    override val isIdentified: Boolean
        get() = true
}
