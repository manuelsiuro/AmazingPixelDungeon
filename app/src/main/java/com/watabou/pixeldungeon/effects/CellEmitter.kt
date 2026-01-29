package com.watabou.pixeldungeon.effects
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.scenes.GameScene
object CellEmitter {
    fun get(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = requireNotNull(GameScene.emitter()) { "GameScene.emitter() must not be null" }
        emitter.pos(p.x, p.y, DungeonTilemap.SIZE.toFloat(), DungeonTilemap.SIZE.toFloat())
        return emitter
    }
    fun center(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = requireNotNull(GameScene.emitter()) { "GameScene.emitter() must not be null" }
        emitter.pos(p.x + DungeonTilemap.SIZE / 2, p.y + DungeonTilemap.SIZE / 2)
        return emitter
    }
    fun bottom(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = requireNotNull(GameScene.emitter()) { "GameScene.emitter() must not be null" }
        emitter.pos(p.x, p.y + DungeonTilemap.SIZE, DungeonTilemap.SIZE.toFloat(), 0f)
        return emitter
    }
}
