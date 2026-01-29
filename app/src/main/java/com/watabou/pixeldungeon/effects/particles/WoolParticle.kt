package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
class WoolParticle : PixelParticle.Shrinking() {
    init {
        color(ColorMath.random(0x999999, 0xEEEEE0))
        acc.set(0f, -40f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        lifespan = Random.Float(0.6f, 1f)
        left = lifespan
        size = 5f
        speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(WoolParticle::class.java) as WoolParticle).reset(x, y)
            }
        }
    }
}
