package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.effects.particles.SparkParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class ThunderAspect : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // Find adjacent enemies to chain to
        val candidates = mutableListOf<Char>()
        for (ofs in Level.NEIGHBOURS8) {
            val pos = defender.pos + ofs
            val ch = Actor.findChar(pos)
            if (ch != null && ch !== attacker && ch !== defender) {
                candidates.add(ch)
            }
        }

        if (candidates.isNotEmpty()) {
            val target = Random.element(candidates)!!
            val chainDamage = damage * 30 / 100
            target.damage(chainDamage, this)
            target.sprite?.centerEmitter()?.burst(SparkParticle.FACTORY, 3)
            target.sprite?.flash()

            // Lightning visual between defender and chain target
            val points = intArrayOf(defender.pos, target.pos)
            attacker.sprite?.parent?.add(Lightning(points, 2, null))

            return true
        }

        return false
    }

    override fun glowing(): ItemSprite.Glowing {
        return YELLOW
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_THUNDERING, weaponName)
    }

    companion object {
        private const val TXT_THUNDERING = "thundering %s"
        private val YELLOW = ItemSprite.Glowing(0xFFFF00)
    }
}
