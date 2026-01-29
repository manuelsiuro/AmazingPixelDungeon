package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Speck
class ParalyticGas : Blob() {
    override fun evolve() {
        super.evolve()
        var ch: com.watabou.pixeldungeon.actors.Char? = null
        for (i in 0 until LENGTH) {
            if (cur[i] > 0 && Actor.findChar(i).also { ch = it } != null) {
                Buffs.prolong(ch!!, Paralysis::class.java, Paralysis.duration(ch!!))
            }
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.PARALYSIS), 0.6f)
    }
    override fun tileDesc(): String {
        return "A cloud of paralytic gas is swirling here."
    }
}
