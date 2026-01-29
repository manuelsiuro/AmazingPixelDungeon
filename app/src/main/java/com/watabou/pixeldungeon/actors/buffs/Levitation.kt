package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
class Levitation : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            target.flying = true
            Buffs.detach(target, Roots::class.java)
            true
        } else {
            false
        }
    }
    override fun detach() {
        val target = target ?: return super.detach()
        target.flying = false
        Dungeon.level?.press(target.pos, target)
        super.detach()
    }
    override fun icon(): Int {
        return BuffIndicator.LEVITATION
    }
    override fun toString(): String {
        return "Levitating"
    }
    companion object {
        const val DURATION = 20f
    }
}
