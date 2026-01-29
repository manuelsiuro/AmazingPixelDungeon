package com.watabou.pixeldungeon.effects
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.utils.Random
class BlobEmitter(private val blob: Blob) : Emitter() {
    init {
        blob.use(this)
    }
    override fun emit(index: Int) {
        if (blob.volume <= 0) {
            return
        }
        val map = blob.cur
        val size = DungeonTilemap.SIZE.toFloat()
        for (i in 0 until LENGTH) {
            if (map[i] > 0 && Dungeon.visible[i]) {
                val x = ((i % WIDTH) + Random.Float()) * size
                val y = ((i / WIDTH) + Random.Float()) * size
                factory?.emit(this, index, x, y)
            }
        }
    }
    companion object {
        private const val WIDTH = Blob.WIDTH
        private const val LENGTH = Blob.LENGTH
    }
}
