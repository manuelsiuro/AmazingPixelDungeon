package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.scenes.GameScene
object FireTrap {
    @Suppress("UNUSED_PARAMETER")
    fun trigger(pos: Int, ch: Char?) {
        Blob.seed(pos, 2, Fire::class.java)?.let { GameScene.add(it) }
        CellEmitter.get(pos).burst(FlameParticle.FACTORY, 5)
    }
}
