package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.particles.LeafParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
class Regrowth : Blob() {
    override fun evolve() {
        super.evolve()
        if (volume > 0) {
            var mapUpdated = false
            for (i in 0 until LENGTH) {
                if (off[i] > 0) {
                    val c = Dungeon.level!!.map[i]
                    var c1 = c
                    if (c == Terrain.EMPTY || c == Terrain.EMBERS || c == Terrain.EMPTY_DECO) {
                        c1 = if (cur[i] > 9) Terrain.HIGH_GRASS else Terrain.GRASS
                    } else if (c == Terrain.GRASS && cur[i] > 9) {
                        c1 = Terrain.HIGH_GRASS
                    }
                    if (c1 != c) {
                        Level.set(i, Terrain.HIGH_GRASS)
                        mapUpdated = true
                        GameScene.updateMap(i)
                        if (Dungeon.visible[i]) {
                            GameScene.discoverTile(i, c)
                        }
                    }
                    val ch = Actor.findChar(i)
                    if (ch != null) {
                        Buffs.prolong(ch, Roots::class.java, TICK)
                    }
                }
            }
            if (mapUpdated) {
                Dungeon.observe()
            }
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(LeafParticle.LEVEL_SPECIFIC, 0.2f, 0)
    }
}
