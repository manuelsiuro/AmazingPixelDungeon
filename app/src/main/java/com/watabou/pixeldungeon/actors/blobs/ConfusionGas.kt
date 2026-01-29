package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Vertigo
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Speck
class ConfusionGas : Blob() {
    override fun evolve() {
        super.evolve()
        var ch: com.watabou.pixeldungeon.actors.Char? = null
        for (i in 0 until LENGTH) {
            if (cur[i] > 0 && Actor.findChar(i).also { ch = it } != null) {
                Buffs.prolong(ch!!, Vertigo::class.java, Vertigo.duration(ch!!))
            }
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.CONFUSION, true), 0.6f)
    }
    override fun tileDesc(): String {
        return "A cloud of confusion gas is swirling here."
    }
}
