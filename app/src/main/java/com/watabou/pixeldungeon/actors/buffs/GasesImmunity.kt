package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.ui.BuffIndicator
class GasesImmunity : FlavourBuff() {
    override fun icon(): Int {
        return BuffIndicator.IMMUNITY
    }
    override fun toString(): String {
        return "Immune to gases"
    }
    companion object {
        const val DURATION = 5f
        val IMMUNITIES = hashSetOf<Class<*>>(
            Paralysis::class.java,
            ToxicGas::class.java,
            Vertigo::class.java
        )
    }
}
