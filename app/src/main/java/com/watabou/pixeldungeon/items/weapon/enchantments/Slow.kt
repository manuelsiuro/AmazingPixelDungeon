package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
import com.watabou.pixeldungeon.actors.buffs.Slow as SlowBuff
class Slow : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 4) >= 3) {
            Buffs.prolong(defender, SlowBuff::class.java,
                    Random.Float(1f, 1.5f + level))
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return BLUE
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_CHILLING, weaponName)
    }
    companion object {
        private const val TXT_CHILLING = "chilling %s"
        private val BLUE = ItemSprite.Glowing(0x0044FF)
    }
}
