package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
class Awareness : FlavourBuff() {
    override fun detach() {
        super.detach()
        Dungeon.observe()
    }
    companion object {
        const val DURATION: Float = 2f
    }
}
