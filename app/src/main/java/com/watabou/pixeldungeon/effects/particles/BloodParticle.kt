package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
class BloodParticle : PixelParticle.Shrinking() {
    init {
        color(0xCC0000)
        lifespan = 0.8f
        acc.set(0f, +40f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = lifespan
        size = 4f
        speed.set(0f)
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p > 0.6f) (1 - p) * 2.5f else 1f
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(BloodParticle::class.java) as BloodParticle).reset(x, y)
            }
        }
    }
}
