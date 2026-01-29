package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Dagger : MeleeWeapon(1, 1.2f, 1f) {
    init {
        name = "dagger"
        image = ItemSpriteSheet.DAGGER
    }
    override fun desc(): String {
        return "A simple iron dagger with a well worn wooden handle."
    }
}
