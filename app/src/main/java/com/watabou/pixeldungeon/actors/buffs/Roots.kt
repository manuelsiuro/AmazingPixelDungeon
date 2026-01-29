package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
class Roots : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (!target.flying && super.attachTo(target)) {
            target.rooted = true
            true
        } else {
            false
        }
    }
    override fun detach() {
        target?.let { 
            it.rooted = false
        }
        super.detach()
    }
    override fun icon(): Int {
        return BuffIndicator.ROOTS
    }
    override fun toString(): String {
        return "Rooted"
    }
}
