package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
open class Invisibility : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            target.invisible++
            true
        } else {
            false
        }
    }
    override fun detach() {
        target?.let { 
            it.invisible--
        }
        super.detach()
    }
    override fun icon(): Int {
        return BuffIndicator.INVISIBLE
    }
    override fun toString(): String {
        return "Invisible"
    }
    companion object {
        const val DURATION = 15f
        fun dispel() {
            val hero = Dungeon.hero ?: return
            val buff = hero.buff(Invisibility::class.java)
            if (buff != null && hero.visibleEnemies() > 0) {
                buff.detach()
            }
        }
    }
}
