package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Vorpal : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())

        // 10% + 2% per level chance to deal triple damage
        if (Random.Int(100) < 10 + level * 2) {
            defender.damage(damage * 2, this)
            defender.sprite?.showStatus(CharSprite.NEGATIVE, "critical!")
            return true
        }

        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return CRIMSON
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_VORPAL, weaponName)
    }

    companion object {
        private const val TXT_VORPAL = "vorpal %s"
        private val CRIMSON = ItemSprite.Glowing(0x880000)
    }
}
