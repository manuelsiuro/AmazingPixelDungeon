package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
class Rage : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.RAGE
    }
    override fun toString(): String {
        return "Blinded with rage"
    }
}
