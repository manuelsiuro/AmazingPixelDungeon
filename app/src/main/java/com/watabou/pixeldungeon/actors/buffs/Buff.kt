package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
open class Buff : Actor() {
    var target: Char? = null
    open fun attachTo(target: Char): Boolean {
        if (target.immunities().contains(javaClass)) {
            return false
        }
        this.target = target
        target.add(this)
        return true
    }
    open fun detach() {
        target?.remove(this)
    }
    override fun act(): Boolean {
        diactivate()
        return true
    }
    open fun icon(): Int {
        return BuffIndicator.NONE
    }
    companion object {
        const val TICK = Actor.TICK
        // Static methods moved to Buffs object to avoid IR lowering bug
    }
}
