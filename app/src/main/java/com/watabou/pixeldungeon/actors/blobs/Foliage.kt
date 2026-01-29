package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Shadows
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.particles.ShaftParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
class Foliage : Blob() {
    override fun evolve() {
        val from = WIDTH + 1
        val to = Level.LENGTH - WIDTH - 1
        val map = Dungeon.level!!.map
        var regrowth = false
        var visible = false
        for (pos in from until to) {
            if (cur[pos] > 0) {
                off[pos] = cur[pos]
                volume += off[pos]
                if (map[pos] == Terrain.EMBERS) {
                    map[pos] = Terrain.GRASS
                    regrowth = true
                }
                visible = visible || Dungeon.visible[pos]
            } else {
                off[pos] = 0
            }
        }
        val hero = Dungeon.hero!!
        if (hero.isAlive && hero.visibleEnemies() == 0 && cur[hero.pos] > 0) {
            Buffs.affect(hero, Shadows::class.java)!!.prolong()
        }
        if (regrowth) {
            GameScene.updateMap()
        }
        if (visible) {
            Journal.add(Journal.Feature.GARDEN)
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(ShaftParticle.FACTORY, 0.9f, 0)
    }
    override fun tileDesc(): String {
        return "Shafts of light pierce the gloom of the underground garden."
    }
}
