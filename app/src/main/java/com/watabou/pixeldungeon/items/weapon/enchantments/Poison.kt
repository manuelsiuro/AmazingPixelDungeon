package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
import com.watabou.pixeldungeon.actors.buffs.Poison as PoisonBuff
class Poison : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 3) >= 2) {
            Buffs.affect(defender, PoisonBuff::class.java)?.set(PoisonBuff.durationFactor(defender) * (level + 1))
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return PURPLE
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_VENOMOUS, weaponName)
    }
    companion object {
        private const val TXT_VENOMOUS = "venomous %s"
        private val PURPLE = ItemSprite.Glowing(0x4400AA)
    }
}
