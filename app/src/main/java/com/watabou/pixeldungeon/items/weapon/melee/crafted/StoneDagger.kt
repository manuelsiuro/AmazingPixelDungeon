package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class StoneDagger : MeleeWeapon(2, 1.2f, 0.8f) {
    init {
        name = "stone dagger"
        image = ItemSpriteSheet.STONE_DAGGER
        STR = 10
    }

    override fun desc(): String =
        "A chipped stone blade lashed to a short handle. Fast and light, but fragile."

    override val isIdentified: Boolean
        get() = true
}
