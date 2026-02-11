package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Scimitar : MeleeWeapon(3, 1.2f, 1.0f) {
    init {
        name = "scimitar"
        image = ItemSpriteSheet.SCIMITAR
    }

    override fun desc(): String {
        return "The curved blade of this elegant sword allows for precise, sweeping strikes that rarely miss their mark."
    }
}
