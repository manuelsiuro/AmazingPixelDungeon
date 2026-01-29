package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Knuckles : MeleeWeapon(1, 1f, 0.5f) {
    init {
        name = "knuckleduster"
        image = ItemSpriteSheet.KNUCKLEDUSTER
    }
    override fun desc(): String {
        return "A piece of iron shaped to fit around the knuckles."
    }
}
