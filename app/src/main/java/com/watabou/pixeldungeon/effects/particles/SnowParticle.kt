package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class SnowParticle : PixelParticle() {
    init {
        speed.set(0f, Random.Float(5f, 8f))
        lifespan = 1.2f
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y - speed.y * lifespan
        left = lifespan
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = (if (p < 0.5f) p else 1 - p) * 1.5f
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(SnowParticle::class.java) as SnowParticle).reset(x, y)
            }
        }
    }
}
