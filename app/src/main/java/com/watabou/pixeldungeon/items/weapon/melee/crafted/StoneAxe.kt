package com.watabou.pixeldungeon.items.weapon.melee.crafted

import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class StoneAxe : MeleeWeapon(2, 1f, 1.1f) {
    init {
        name = "stone axe"
        image = ItemSpriteSheet.STONE_AXE
        STR = 12
    }

    override fun desc(): String =
        "A primitive stone axe, its heavy head bound to a wooden shaft with plant fibers. Slow but powerful."

    override val isIdentified: Boolean
        get() = true
}
