package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite

class Reach : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return FOREST_GREEN
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_ELONGATED, weaponName)
    }

    companion object {
        private const val TXT_ELONGATED = "elongated %s"
        private val FOREST_GREEN = ItemSprite.Glowing(0x44AA44)
    }
}
