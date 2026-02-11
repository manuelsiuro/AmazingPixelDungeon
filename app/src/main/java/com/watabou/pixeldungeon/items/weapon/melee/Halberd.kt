package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Halberd : MeleeWeapon(4, 1.0f, 1.5f) {
    init {
        name = "halberd"
        image = ItemSpriteSheet.HALBERD
    }

    override fun desc(): String {
        return "This polearm combines an axe blade with a spear tip. Slow to swing, but each blow lands with devastating force."
    }
}
