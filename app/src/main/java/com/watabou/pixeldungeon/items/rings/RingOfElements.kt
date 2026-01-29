package com.watabou.pixeldungeon.items.rings
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.mobs.Eye
import com.watabou.pixeldungeon.actors.mobs.Warlock
import com.watabou.pixeldungeon.actors.mobs.Yog
import com.watabou.pixeldungeon.levels.traps.LightningTrap
import com.watabou.utils.Random
import java.util.HashSet
class RingOfElements : Ring() {
    init {
        name = "Ring of Elements"
    }
    override fun buff(): RingBuff {
        return Resistance()
    }
    override fun desc(): String {
        return if (isKnown())
            "This ring provides resistance to different elements, such as fire, " +
                    "electricity, gases etc. Also it decreases duration of negative effects."
        else
            super.desc()
    }
    inner class Resistance : RingBuff() {
        fun resistances(): HashSet<Class<*>> {
            return if (Random.Int(level + 3) >= 3) {
                FULL
            } else {
                EMPTY
            }
        }
        fun durationFactor(): Float {
            return if (level < 0) 1f else (2 + 0.5f * level) / (2 + level)
        }
    }
    companion object {
        private val EMPTY = HashSet<Class<*>>()
        private val FULL = HashSet<Class<*>>()
        init {
            FULL.add(Burning::class.java)
            FULL.add(ToxicGas::class.java)
            FULL.add(Poison::class.java)
            FULL.add(LightningTrap.Electricity::class.java)
            FULL.add(Warlock::class.java)
            FULL.add(Eye::class.java)
            FULL.add(Yog.BurningFist::class.java)
        }
    }
}
