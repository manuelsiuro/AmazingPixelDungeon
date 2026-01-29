package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
class Amok : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.AMOK
    }
    override fun toString(): String {
        return "Amok"
    }
}
