package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite

class SweepingEdge : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val dx = (defender.pos % Level.WIDTH) - (attacker.pos % Level.WIDTH)
        val dy = (defender.pos / Level.WIDTH) - (attacker.pos / Level.WIDTH)

        // Normalize to -1, 0, 1
        val ndx = if (dx > 0) 1 else if (dx < 0) -1 else 0
        val ndy = if (dy > 0) 1 else if (dy < 0) -1 else 0

        // Perpendicular offsets from defender position
        val perp1 = defender.pos + (-ndy) + (ndx) * Level.WIDTH
        val perp2 = defender.pos + (ndy) + (-ndx) * Level.WIDTH

        var hit = false

        for (pos in intArrayOf(perp1, perp2)) {
            val ch = Actor.findChar(pos)
            if (ch != null && ch !== attacker) {
                ch.damage(damage / 2, this)
                ch.sprite?.flash()
                hit = true
            }
        }

        return hit
    }

    override fun glowing(): ItemSprite.Glowing {
        return PURPLE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_SWEEPING, weaponName)
    }

    companion object {
        private const val TXT_SWEEPING = "sweeping %s"
        private val PURPLE = ItemSprite.Glowing(0x8800CC)
    }
}
