package com.watabou.pixeldungeon.items.weapon.melee

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Greataxe : MeleeWeapon(5, 0.8f, 1.5f) {
    init {
        name = "greataxe"
        image = ItemSpriteSheet.GREATAXE
        STR = 20
    }

    override fun desc(): String {
        return "An enormous double-headed axe that requires tremendous strength to wield. Its massive blade cleaves through anything in its path."
    }
}
