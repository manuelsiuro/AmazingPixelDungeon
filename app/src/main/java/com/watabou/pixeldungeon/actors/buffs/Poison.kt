package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
class Poison : Buff(), Hero.Doom {
    private var left = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(TAG_LEFT, left)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(TAG_LEFT)
    }
    fun set(duration: Float) {
        left = duration
    }
    override fun icon(): Int {
        return BuffIndicator.POISON
    }
    override fun toString(): String {
        return "Poisoned"
    }
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            target.damage((left / 3).toInt() + 1, this)
            spend(TICK)
            left -= TICK
            if (left <= 0) {
                detach()
            }
        } else {
            detach()
        }
        return true
    }
    override fun onDeath() {
        Badges.validateDeathFromPoison()
        Dungeon.fail(Utils.format(ResultDescriptions.POISON, Dungeon.depth))
        GLog.n("You died from poison...")
    }
    companion object {
        private const val TAG_LEFT = "left"
        fun durationFactor(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return r?.durationFactor() ?: 1f
        }
    }
}
