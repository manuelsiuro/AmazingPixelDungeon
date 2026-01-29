package com.watabou.pixeldungeon.levels.features
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
object Door {
    fun enter(pos: Int) {
        Level.set(pos, Terrain.OPEN_DOOR)
        GameScene.updateMap(pos)
        Dungeon.observe()
        if (Dungeon.visible[pos]) {
            Sample.play(Assets.SND_OPEN)
        }
    }
    fun leave(pos: Int) {
        val level = Dungeon.level ?: return
        if (level.heaps[pos] == null) {
            Level.set(pos, Terrain.DOOR)
            GameScene.updateMap(pos)
            Dungeon.observe()
        }
    }
}
