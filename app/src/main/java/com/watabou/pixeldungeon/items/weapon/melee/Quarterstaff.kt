package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Quarterstaff : MeleeWeapon(2, 1f, 1f) {
    init {
        name = "quarterstaff"
        image = ItemSpriteSheet.QUARTERSTAFF
    }
    override fun desc(): String {
        return "A staff of hardwood, its ends are shod with iron."
    }
}
