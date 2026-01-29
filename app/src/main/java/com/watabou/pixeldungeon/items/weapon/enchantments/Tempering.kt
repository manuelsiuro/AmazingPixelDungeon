package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
class Tempering : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        weapon.polish()
        return true
    }
    override fun glowing(): ItemSprite.Glowing {
        return GRAY
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_TEMPERED, weaponName)
    }
    companion object {
        private const val TXT_TEMPERED = "tempered %s"
        private val GRAY = ItemSprite.Glowing(0xCC8888)
    }
}
