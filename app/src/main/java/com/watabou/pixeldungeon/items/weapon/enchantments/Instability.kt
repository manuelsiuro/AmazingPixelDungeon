package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
class Instability : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // Safe check for random() returning null
        return random()?.proc(weapon, attacker, defender, damage) == true
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_UNSTABLE, weaponName)
    }
    companion object {
        private const val TXT_UNSTABLE = "unstable %s"
    }
}
