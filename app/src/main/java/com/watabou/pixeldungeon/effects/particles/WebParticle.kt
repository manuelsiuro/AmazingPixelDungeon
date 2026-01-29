package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class WebParticle : PixelParticle() {
    init {
        color(0xCCCCCC)
        lifespan = 2f
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = lifespan
        angle = Random.Float(360f)
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p < 0.5f) p else 1 - p
        scale.y = 16 + p * 8
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                for (i in 0 until 3) {
                    (emitter.recycle(WebParticle::class.java) as WebParticle).reset(x, y)
                }
            }
        }
    }
}
