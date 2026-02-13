package com.watabou.pixeldungeon.actors.buffs

import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
import kotlin.math.min

class WellFed : Buff() {

    private var left = 0f

    fun set(duration: Float) {
        left = duration
    }

    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            if (target.HP < target.HT) {
                target.HP = min(target.HP + 1, target.HT)
            }
            spend(TICK_INTERVAL)
            left -= TICK_INTERVAL
            if (left <= 0) {
                detach()
            }
        } else {
            detach()
        }
        return true
    }

    override fun icon(): Int = BuffIndicator.WELL_FED

    override fun toString(): String = "Well Fed"

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }

    companion object {
        private const val TICK_INTERVAL = 5f
        private const val LEFT = "left"
    }
}
