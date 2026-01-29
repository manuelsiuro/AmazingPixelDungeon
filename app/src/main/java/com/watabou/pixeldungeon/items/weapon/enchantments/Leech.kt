package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Leech : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        val level = Math.max(0, weapon.effectiveLevel())
        // lvl 0 - 33%
        // lvl 1 - 43%
        // lvl 2 - 50%
        val maxValue = damage * (level + 2) / (level + 6)
        val effValue = Math.min(Random.IntRange(0, maxValue), attacker.HT - attacker.HP)
        if (effValue > 0) {
            attacker.HP += effValue
            attacker.sprite?.emitter()?.start(Speck.factory(Speck.HEALING), 0.4f, 1)
            attacker.sprite?.showStatus(CharSprite.POSITIVE, Integer.toString(effValue))
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return RED
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_VAMPIRIC, weaponName)
    }
    companion object {
        private const val TXT_VAMPIRIC = "vampiric %s"
        private val RED = ItemSprite.Glowing(0x660022)
    }
}
