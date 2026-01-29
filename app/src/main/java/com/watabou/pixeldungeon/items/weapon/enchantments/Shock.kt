package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.effects.particles.SparkParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.traps.LightningTrap
import com.watabou.utils.Random
import java.util.ArrayList
import java.util.HashSet
class Shock : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 25%
        // lvl 1 - 40%
        // lvl 2 - 50%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 4) >= 3) {
            points[0] = attacker.pos
            nPoints = 1
            affected.clear()
            affected.add(attacker)
            hit(defender, Random.Int(1, damage / 2))
            attacker.sprite?.parent?.add(Lightning(points, nPoints, null))
            return true
        } else {
            return false
        }
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_SHOCKING, weaponName)
    }
    private val affected = ArrayList<Char>()
    private val points = IntArray(20)
    private var nPoints: Int = 0
    private fun hit(ch: Char, damage: Int) {
        if (damage < 1) {
            return
        }
        affected.add(ch)
        ch.damage(if (Level.water[ch.pos] && !ch.flying) (damage * 2) else damage, LightningTrap.LIGHTNING)
        ch.sprite?.centerEmitter()?.burst(SparkParticle.FACTORY, 3)
        ch.sprite?.flash()
        points[nPoints++] = ch.pos
        val ns = HashSet<Char>()
        for (i in Level.NEIGHBOURS8.indices) {
            val n = Actor.findChar(ch.pos + Level.NEIGHBOURS8[i])
            if (n != null && !affected.contains(n)) {
                ns.add(n)
            }
        }
        if (ns.size > 0) {
            Random.element(ns)?.let { hit(it, Random.Int(damage / 2, damage)) }
        }
    }
    companion object {
        private const val TXT_SHOCKING = "shocking %s"
    }
}
