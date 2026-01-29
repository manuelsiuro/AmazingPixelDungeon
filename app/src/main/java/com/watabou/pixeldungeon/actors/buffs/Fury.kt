package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
class Fury : Buff() {
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.HP > target.HT * LEVEL) {
            detach()
        }
        spend(TICK)
        return true
    }
    override fun icon(): Int {
        return BuffIndicator.FURY
    }
    override fun toString(): String {
        return "Fury"
    }
    companion object {
        const val LEVEL = 0.4f
    }
}
