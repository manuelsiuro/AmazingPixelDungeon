package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class BlastParticle : PixelParticle.Shrinking() {
    init {
        color(0xEE7722)
        acc.set(0f, +50f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = Random.Float()
        lifespan = left
        size = 8f
        speed.polar(-Random.Float(3.1415926f), Random.Float(32f, 64f))
    }
    override fun update() {
        super.update()
        am = if (left > 0.8f) (1 - left) * 5 else 1f
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(BlastParticle::class.java) as BlastParticle).reset(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
