package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Draining : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())

        // 33% + scaling chance to proc
        if (Random.Int(level + 3) >= 2) {
            Buffs.prolong(defender, Weakness::class.java, 3f + level)
            return true
        }

        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return PURPLE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_DRAINING, weaponName)
    }

    companion object {
        private const val TXT_DRAINING = "draining %s"
        private val PURPLE = ItemSprite.Glowing(0x6600CC)
    }
}
