package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Fire : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 33%
        // lvl 1 - 50%
        // lvl 2 - 60%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 3) >= 2) {
            if (Random.Int(2) == 0) {
                Buffs.affect(defender, Burning::class.java)?.reignite(defender)
            }
            defender.damage(Random.Int(1, level + 2), this)
            defender.sprite?.emitter()?.burst(FlameParticle.FACTORY, level + 1)
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return ORANGE
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_BLAZING, weaponName)
    }
    companion object {
        private const val TXT_BLAZING = "blazing %s"
        private val ORANGE = ItemSprite.Glowing(0xFF4400)
    }
}
