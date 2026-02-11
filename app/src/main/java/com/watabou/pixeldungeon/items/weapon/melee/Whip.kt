package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Whip : MeleeWeapon(2, 0.8f, 0.8f) {
    init {
        name = "whip"
        image = ItemSpriteSheet.WHIP
    }

    override fun desc(): String {
        return "A long leather whip that strikes with blinding speed, though its lashes lack the precision of a blade."
    }
}
