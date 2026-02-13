package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite

class Soulbound : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return GOLD
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_SOULBOUND, weaponName)
    }

    companion object {
        private const val TXT_SOULBOUND = "soulbound %s"
        private val GOLD = ItemSprite.Glowing(0xFFDD00)
    }
}
