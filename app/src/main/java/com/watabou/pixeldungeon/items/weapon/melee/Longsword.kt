package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Longsword : MeleeWeapon(4, 1f, 1f) {
    init {
        name = "longsword"
        image = ItemSpriteSheet.LONG_SWORD
    }
    override fun desc(): String {
        return "This towering blade inflicts heavy damage by investing its heft into every cut."
    }
}
