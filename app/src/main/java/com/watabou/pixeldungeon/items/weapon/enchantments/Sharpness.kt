package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Sharpness : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        defender.damage(Random.IntRange(1, 2), this)
        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return STEEL_BLUE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_KEEN, weaponName)
    }

    companion object {
        private const val TXT_KEEN = "keen %s"
        private val STEEL_BLUE = ItemSprite.Glowing(0x6688CC)
    }
}
