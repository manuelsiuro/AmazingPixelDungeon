package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Luck : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())
        var dmg = damage
        for (i in 1..level + 1) {
            dmg = Math.max(dmg, attacker.damageRoll() - i)
        }
        if (dmg > damage) {
            defender.damage(dmg - damage, this)
            return true
        } else {
            return false
        }
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_LUCKY, weaponName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return GREEN
    }
    companion object {
        private const val TXT_LUCKY = "lucky %s"
        private val GREEN = ItemSprite.Glowing(0x00FF00)
    }
}
