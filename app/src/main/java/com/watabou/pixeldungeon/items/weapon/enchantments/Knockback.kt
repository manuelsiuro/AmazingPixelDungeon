package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite

class Knockback : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val dx = (defender.pos % Level.WIDTH) - (attacker.pos % Level.WIDTH)
        val dy = (defender.pos / Level.WIDTH) - (attacker.pos / Level.WIDTH)

        val ndx = if (dx > 0) 1 else if (dx < 0) -1 else 0
        val ndy = if (dy > 0) 1 else if (dy < 0) -1 else 0

        val targetPos = defender.pos + ndx + ndy * Level.WIDTH

        if (Level.passable[targetPos] && Actor.findChar(targetPos) == null) {
            val oldPos = defender.pos
            defender.pos = targetPos
            defender.sprite?.move(oldPos, targetPos)
            Dungeon.level?.press(targetPos, defender)
            return true
        }

        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return SILVER
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_FORCEFUL, weaponName)
    }

    companion object {
        private const val TXT_FORCEFUL = "forceful %s"
        private val SILVER = ItemSprite.Glowing(0xCCCCDD)
    }
}
