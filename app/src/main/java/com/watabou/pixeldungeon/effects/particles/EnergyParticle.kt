package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random
class EnergyParticle : PixelParticle() {
    init {
        lifespan = 1f
        color(0xFFFFAA)
        speed.polar(Random.Float(PointF.PI2), Random.Float(24f, 32f))
    }
    fun reset(x: Float, y: Float) {
        revive()
        left = lifespan
        this.x = x - speed.x * lifespan
        this.y = y - speed.y * lifespan
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p < 0.5f) p * p * 4 else (1 - p) * 2
        size(Random.Float(5 * left / lifespan))
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(EnergyParticle::class.java) as EnergyParticle).reset(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
