package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
class Fire : Blob() {
    override fun evolve() {
        val flamable = Level.flamable
        val from = WIDTH + 1
        val to = Level.LENGTH - WIDTH - 1
        var observe = false
        for (pos in from until to) {
            val fire: Int
            if (cur[pos] > 0) {
                burn(pos)
                fire = cur[pos] - 1
                if (fire <= 0 && flamable[pos]) {
                    val oldTile = Dungeon.level!!.map[pos]
                    Dungeon.level!!.destroy(pos)
                    observe = true
                    GameScene.updateMap(pos)
                    if (Dungeon.visible[pos]) {
                        GameScene.discoverTile(pos, oldTile)
                    }
                }
            } else {
                if (flamable[pos] && (cur[pos - 1] > 0 || cur[pos + 1] > 0 || cur[pos - WIDTH] > 0 || cur[pos + WIDTH] > 0)) {
                    fire = 4
                    burn(pos)
                } else {
                    fire = 0
                }
            }
            volume += off[pos].let { off[pos] = fire; fire }
        }
        if (observe) {
            Dungeon.observe()
        }
    }
    private fun burn(pos: Int) {
        val ch = Actor.findChar(pos)
        if (ch != null) {
            Buffs.affect(ch, Burning::class.java)!!.reignite(ch)
        }
        val heap = Dungeon.level!!.heaps.get(pos)
        if (heap != null) {
            heap.burn()
        }
    }
    override fun seed(cell: Int, amount: Int) {
        if (cur[cell] == 0) {
            volume += amount
            cur[cell] = amount
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(FlameParticle.FACTORY, 0.03f, 0)
    }
    override fun tileDesc(): String {
        return "A fire is raging here."
    }
}
