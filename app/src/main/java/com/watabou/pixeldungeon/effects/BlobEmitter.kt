package com.watabou.pixeldungeon.effects
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.levels.Level
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
        val w = Level.WIDTH
        val len = Level.LENGTH
        for (i in 0 until len) {
            if (i < map.size && map[i] > 0 && i < Dungeon.visible.size && Dungeon.visible[i]) {
                val x = ((i % w) + Random.Float()) * size
                val y = ((i / w) + Random.Float()) * size
                factory?.emit(this, index, x, y)
            }
        }
    }
}
