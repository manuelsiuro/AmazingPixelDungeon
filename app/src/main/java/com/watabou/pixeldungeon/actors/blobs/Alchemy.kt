package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.utils.Bundle
class Alchemy : Blob() {
    protected var pos: Int = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur[i] > 0) {
                pos = i
                break
            }
        }
    }
    override fun evolve() {
        volume = cur[pos].also { off[pos] = it }
        if (Dungeon.visible[pos]) {
            Journal.add(Journal.Feature.ALCHEMY)
        }
    }
    override fun seed(cell: Int, amount: Int) {
        cur[pos] = 0
        pos = cell
        volume = amount.also { cur[pos] = it }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.BUBBLE), 0.4f, 0)
    }
    companion object {
        fun transmute(cell: Int) {
            val heap = Dungeon.level!!.heaps.get(cell)
            if (heap != null) {
                val result = heap.transmute()
                if (result != null) {
                    Dungeon.level!!.drop(result, cell).sprite?.drop(cell)
                }
            }
        }
    }
}
