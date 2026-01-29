package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Frost
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.SnowParticle
import com.watabou.utils.Random
object Freezing {
    // Returns true, if this cell is visible
    fun affect(cell: Int, fire: Fire?): Boolean {
        val ch = Actor.findChar(cell)
        if (ch != null) {
            Buffs.prolong(ch, Frost::class.java, Frost.duration(ch) * Random.Float(1.0f, 1.5f))
        }
        if (fire != null) {
            fire.clear(cell)
        }
        val heap = Dungeon.level!!.heaps.get(cell)
        if (heap != null) {
            heap.freeze()
        }
        return if (Dungeon.visible[cell]) {
            CellEmitter.get(cell).start(SnowParticle.FACTORY, 0.2f, 6)
            true
        } else {
            false
        }
    }
}
