package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Sword : MeleeWeapon(3, 1f, 1f) {
    init {
        name = "sword"
        image = ItemSpriteSheet.SWORD
    }
    override fun desc(): String {
        return "The razor-sharp length of steel blade shines reassuringly."
    }
}
