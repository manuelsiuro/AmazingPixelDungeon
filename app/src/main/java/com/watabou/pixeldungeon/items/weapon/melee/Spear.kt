package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Spear : MeleeWeapon(2, 1f, 1.5f) {
    init {
        name = "spear"
        image = ItemSpriteSheet.SPEAR
    }
    override fun desc(): String {
        return "A slender wooden rod tipped with sharpened iron."
    }
}
