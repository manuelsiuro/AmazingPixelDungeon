package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PoisonParticle
object PoisonTrap {
    fun trigger(pos: Int, ch: Char?) {
        if (ch != null) {
            Buffs.affect(ch, Poison::class.java)?.set(Poison.durationFactor(ch) * (4 + Dungeon.depth / 2))
        }
        CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3)
    }
}
