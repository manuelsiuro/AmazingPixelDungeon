package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Chaining : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())

        // 25% + 5% per level chance to chain
        if (Random.Int(level + 4) >= 3) {

            // Find an adjacent enemy to chain to
            for (ofs in Level.NEIGHBOURS8) {
                val pos = defender.pos + ofs
                val ch = Actor.findChar(pos)
                if (ch != null && ch !== attacker && ch !== defender) {
                    val chainDamage = damage / 2
                    ch.damage(chainDamage, this)
                    ch.sprite?.flash()

                    // Lightning visual between targets
                    val points = intArrayOf(defender.pos, ch.pos)
                    attacker.sprite?.parent?.add(Lightning(points, 2, null))

                    return true
                }
            }
        }

        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return ORANGE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_CHAINING, weaponName)
    }

    companion object {
        private const val TXT_CHAINING = "chaining %s"
        private val ORANGE = ItemSprite.Glowing(0xFF6600)
    }
}
