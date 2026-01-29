package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Death : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 8%
        // lvl 1 ~ 9%
        // lvl 2 ~ 10%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 100) >= 92) {
            defender.damage(defender.HP, this)
            defender.sprite?.emitter()?.burst(ShadowParticle.UP, 5)
            if (!defender.isAlive && attacker is Hero) {
                Badges.validateGrimWeapon()
            }
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return BLACK
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_GRIM, weaponName)
    }
    companion object {
        private const val TXT_GRIM = "grim %s"
        private val BLACK = ItemSprite.Glowing(0x000000)
    }
}
