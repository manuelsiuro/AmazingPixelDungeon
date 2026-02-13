package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronSword : MeleeWeapon(3, 1f, 1f) {
    init {
        name = "iron sword"
        image = ItemSpriteSheet.CRAFTED_IRON_SWORD
        STR = 14
    }

    override fun desc(): String =
        "A hand-forged iron sword. The blade is rough but serviceable, a testament to dungeon craftsmanship."

    override val isIdentified: Boolean
        get() = true
}
