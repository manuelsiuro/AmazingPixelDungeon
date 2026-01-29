package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class WarHammer : MeleeWeapon(5, 1.2f, 1f) {
    init {
        name = "war hammer"
        image = ItemSpriteSheet.WAR_HAMMER
    }
    override fun desc(): String {
        return "Few creatures can withstand the crushing blow of this towering mass of lead and steel, " +
                "but only the strongest of adventurers can use it effectively."
    }
}
