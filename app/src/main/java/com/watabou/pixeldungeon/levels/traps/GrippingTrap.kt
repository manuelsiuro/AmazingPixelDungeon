package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.effects.Wound
import com.watabou.utils.Random
import kotlin.math.max
object GrippingTrap {
    fun trigger(pos: Int, c: Char?) {
        if (c != null) {
            val damage = max(0, (Dungeon.depth + 3) - Random.IntRange(0, c.dr() / 2))
            Buffs.affect(c, Bleeding::class.java)?.set(damage)
            Buffs.prolong(c, Cripple::class.java, Cripple.DURATION)
            Wound.hit(c)
        } else {
            Wound.hit(pos)
        }
    }
}
