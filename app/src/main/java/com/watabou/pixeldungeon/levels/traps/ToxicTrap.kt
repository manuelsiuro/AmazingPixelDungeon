package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.scenes.GameScene
object ToxicTrap {
    @Suppress("UNUSED_PARAMETER")
    fun trigger(pos: Int, ch: Char?) {
        Blob.seed(pos, 300 + 20 * Dungeon.depth, ToxicGas::class.java)?.let { GameScene.add(it) }
    }
}
