package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Mace : MeleeWeapon(3, 1f, 0.8f) {
    init {
        name = "mace"
        image = ItemSpriteSheet.MACE
    }
    override fun desc(): String {
        return "The iron head of this weapon inflicts substantial damage."
    }
}
