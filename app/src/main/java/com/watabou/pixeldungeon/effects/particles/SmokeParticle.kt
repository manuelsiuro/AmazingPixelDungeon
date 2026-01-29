package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class SmokeParticle : PixelParticle() {
    init {
        color(0x222222)
        acc.set(0f, -40f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        lifespan = Random.Float(0.6f, 1f)
        left = lifespan
        speed.set(Random.Float(-4f, +4f), Random.Float(-8f, +8f))
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p > 0.8f) 2 - 2 * p else p * 0.5f
        size(16 - p * 8)
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(SmokeParticle::class.java) as SmokeParticle).reset(x, y)
            }
        }
    }
}
