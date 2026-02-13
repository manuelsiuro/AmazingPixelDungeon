package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class DiamondBlade : MeleeWeapon(4, 1.1f, 1f) {
    init {
        name = "diamond blade"
        image = ItemSpriteSheet.DIAMOND_BLADE
        STR = 18
    }

    override fun desc(): String =
        "A masterwork blade edged with diamond shards. It cuts through armor as if it were parchment."

    override val isIdentified: Boolean
        get() = true
}
