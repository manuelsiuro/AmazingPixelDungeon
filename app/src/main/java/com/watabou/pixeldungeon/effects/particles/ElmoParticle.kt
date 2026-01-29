package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
class ElmoParticle : PixelParticle.Shrinking() {
    init {
        color(0x22EE66)
        lifespan = 0.6f
        acc.set(0f, -80f)
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
        am = if (p > 0.8f) (1 - p) * 5 else 1f
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ElmoParticle::class.java) as ElmoParticle).reset(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
