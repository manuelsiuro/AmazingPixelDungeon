package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.utils.PointF
import com.watabou.utils.Random
class WindParticle : PixelParticle() {
    private var sizeVal: Float = 0f
    private var windAngle: Float = 0f
    private var windSpeed: PointF = PointF()
    init {
        lifespan = Random.Float(1f, 2f)
        sizeVal = Random.Float(3f)
        scale.set(sizeVal)
        windAngle = Random.Float(PointF.PI2)
        windSpeed = PointF().polar(windAngle, 5f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        left = lifespan
        super.speed.set(windSpeed)
        super.speed.scale(sizeVal)
        this.x = x - super.speed.x * lifespan / 2
        this.y = y - super.speed.y * lifespan / 2
        windAngle += Random.Float(-0.1f, +0.1f)
        windSpeed = PointF().polar(windAngle, 5f)
        am = 0f
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * sizeVal * 0.2f
    }
    class Wind(private val pos: Int) : Group() {
        private val xPos: Float
        private val yPos: Float
        private var delay: Float = 0f
        init {
            val p = DungeonTilemap.tileToWorld(pos)
            xPos = p.x
            yPos = p.y
            delay = Random.Float(5f)
        }
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                delay -= Game.elapsed
                if (delay <= 0) {
                    delay = Random.Float(5f)
                    (recycle(WindParticle::class.java) as WindParticle).reset(
                        xPos + Random.Float(DungeonTilemap.SIZE.toFloat()),
                        yPos + Random.Float(DungeonTilemap.SIZE.toFloat())
                    )
                }
            }
        }
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(WindParticle::class.java) as WindParticle).reset(x, y)
            }
        }
    }
}
