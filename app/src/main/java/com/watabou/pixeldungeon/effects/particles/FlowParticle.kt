package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.utils.PointF
import com.watabou.utils.Random
class FlowParticle : PixelParticle() {
    init {
        lifespan = 0.6f
        acc.set(0f, 32f)
        angularSpeed = Random.Float(-360f, +360f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        left = lifespan
        this.x = x
        this.y = y
        am = 0f
        size(0f)
        speed.set(0f)
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * 0.6f
        size((1 - p) * 4)
    }
    class Flow(private val pos: Int) : Group() {
        private val xPos: Float
        private val yPos: Float
        private var delay: Float = 0f
        init {
            val p = DungeonTilemap.tileToWorld(pos)
            xPos = p.x
            yPos = p.y + DungeonTilemap.SIZE - 1
            delay = Random.Float(DELAY)
        }
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                delay -= Game.elapsed
                if (delay <= 0) {
                    delay = Random.Float(DELAY)
                    (recycle(FlowParticle::class.java) as FlowParticle).reset(
                        xPos + Random.Float(DungeonTilemap.SIZE.toFloat()), yPos
                    )
                }
            }
        }
        companion object {
            // Renamed to avoid collision or confusion, though private val in Java
            private const val DELAY = 0.1f
        }
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(FlowParticle::class.java) as FlowParticle).reset(x, y)
            }
        }
    }
}
