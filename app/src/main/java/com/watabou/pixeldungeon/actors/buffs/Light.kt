package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
import kotlin.math.max
class Light : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            Dungeon.level?.let { level ->
                target.viewDistance = max(level.viewDistance, DISTANCE)
                Dungeon.observe()
            }
            true
        } else {
            false
        }
    }
    override fun detach() {
        target?.let { t ->
            Dungeon.level?.let { level ->
                t.viewDistance = level.viewDistance
                Dungeon.observe()
            }
        }
        super.detach()
    }
    override fun icon(): Int {
        return BuffIndicator.LIGHT
    }
    override fun toString(): String {
        return "Illuminated"
    }
    companion object {
        const val DURATION = 250f
        const val DISTANCE = 4
    }
}
