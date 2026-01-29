package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.rings.RingOfMending
import kotlin.math.pow
class Regeneration : Buff() {
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            val hero = target as Hero
            if (target.HP < target.HT && !hero.isStarving) {
                target.HP += 1
            }
            var bonus = 0
            for (buff in target.buffs(RingOfMending.Rejuvenation::class.java)) {
                bonus += buff.level
            }
            spend((REGENERATION_DELAY / 1.2.pow(bonus.toDouble())).toFloat())
        } else {
            diactivate()
        }
        return true
    }
    companion object {
        private const val REGENERATION_DELAY = 10f
    }
}
