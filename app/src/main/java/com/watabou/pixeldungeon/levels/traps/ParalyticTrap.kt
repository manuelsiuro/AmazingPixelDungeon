package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ParalyticGas
import com.watabou.pixeldungeon.scenes.GameScene
object ParalyticTrap {
    @Suppress("UNUSED_PARAMETER")
    fun trigger(pos: Int, ch: Char?) {
        Blob.seed(pos, 80 + 5 * Dungeon.depth, ParalyticGas::class.java)?.let { GameScene.add(it) }
    }
}
