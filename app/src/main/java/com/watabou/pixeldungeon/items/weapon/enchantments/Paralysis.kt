package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
import com.watabou.pixeldungeon.actors.buffs.Paralysis as ParalysisBuff
class Paralysis : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 13%
        // lvl 1 - 22%
        // lvl 2 - 30%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 8) >= 7) {
            Buffs.prolong(defender, ParalysisBuff::class.java,
                    Random.Float(1f, 1.5f + level))
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return YELLOW
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_STUNNING, weaponName)
    }
    companion object {
        private const val TXT_STUNNING = "stunning %s"
        private val YELLOW = ItemSprite.Glowing(0xCCAA44)
    }
}
