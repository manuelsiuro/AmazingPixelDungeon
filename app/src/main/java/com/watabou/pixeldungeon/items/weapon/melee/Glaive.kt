package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Glaive : MeleeWeapon(5, 1f, 1f) {
    init {
        name = "glaive"
        image = ItemSpriteSheet.GLAIVE
    }
    override fun desc(): String {
        return "A polearm consisting of a sword blade on the end of a pole."
    }
}
