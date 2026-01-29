package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ui.BuffIndicator
class Blindness : FlavourBuff() {
    override fun detach() {
        super.detach()
        Dungeon.observe()
    }
    override fun icon(): Int {
        return BuffIndicator.BLINDNESS
    }
    override fun toString(): String {
        return "Blinded"
    }
}
