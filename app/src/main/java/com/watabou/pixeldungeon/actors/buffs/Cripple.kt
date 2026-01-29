package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.ui.BuffIndicator
class Cripple : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.CRIPPLE
    }
    override fun toString(): String {
        return "Crippled"
    }
    companion object {
        const val DURATION = 10f
    }
}
