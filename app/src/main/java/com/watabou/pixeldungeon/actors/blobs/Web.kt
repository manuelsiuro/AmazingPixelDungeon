package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.particles.WebParticle
class Web : Blob() {
    override fun evolve() {
        for (i in 0 until LENGTH) {
            val offv = if (cur[i] > 0) cur[i] - 1 else 0
            off[i] = offv
            if (offv > 0) {
                volume += offv
                val ch = Actor.findChar(i)
                if (ch != null) {
                    Buffs.prolong(ch, Roots::class.java, TICK)
                }
            }
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(WebParticle.FACTORY, 0.4f)
    }
    override fun seed(cell: Int, amount: Int) {
        val diff = amount - cur[cell]
        if (diff > 0) {
            cur[cell] = amount
            volume += diff
        }
    }
    override fun tileDesc(): String {
        return "Everything is covered with a thick web here."
    }
}
