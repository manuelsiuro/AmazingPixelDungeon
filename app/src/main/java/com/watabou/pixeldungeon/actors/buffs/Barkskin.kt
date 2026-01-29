package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
class Barkskin : Buff() {
    private var level = 0
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            spend(TICK)
            if (--level <= 0) {
                detach()
            }
        } else {
            detach()
        }
        return true
    }
    fun level(): Int {
        return level
    }
    fun level(value: Int) {
        if (level < value) {
            level = value
        }
    }
    override fun icon(): Int {
        return BuffIndicator.BARKSKIN
    }
    override fun toString(): String {
        return "Barkskin"
    }
}
