package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ui.BuffIndicator
class MindVision : FlavourBuff() {
    var distance = 2
    override fun icon(): Int {
        return BuffIndicator.MIND_VISION
    }
    override fun toString(): String {
        return "Mind vision"
    }
    override fun detach() {
        super.detach()
        Dungeon.observe()
    }
    companion object {
        const val DURATION = 20f
    }
}
