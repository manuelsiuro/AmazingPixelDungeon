package com.watabou.pixeldungeon.actors.buffs
open class FlavourBuff : Buff() {
    override fun act(): Boolean {
        detach()
        return true
    }
}
