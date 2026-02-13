package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite

class Vampirism : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val heal = Math.max(1, damage * 15 / 100)
        val effHeal = Math.min(heal, attacker.HT - attacker.HP)

        if (effHeal > 0) {
            attacker.HP += effHeal
            attacker.sprite?.emitter()?.start(Speck.factory(Speck.HEALING), 0.4f, 1)
            attacker.sprite?.showStatus(CharSprite.POSITIVE, effHeal.toString())
        }

        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return BLOOD_RED
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_SANGUINE, weaponName)
    }

    companion object {
        private const val TXT_SANGUINE = "sanguine %s"
        private val BLOOD_RED = ItemSprite.Glowing(0x880022)
    }
}
