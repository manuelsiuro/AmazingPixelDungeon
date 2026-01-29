package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random
class ShadowParticle : PixelParticle.Shrinking() {
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        speed.set(Random.Float(-5f, +5f), Random.Float(-5f, +5f))
        size = 6f
        lifespan = 0.5f
        left = lifespan
    }
    fun resetCurse(x: Float, y: Float) {
        revive()
        size = 8f
        lifespan = 0.5f
        left = lifespan
        speed.polar(Random.Float(PointF.PI2), Random.Float(16f, 32f))
        this.x = x - speed.x * lifespan
        this.y = y - speed.y * lifespan
    }
    fun resetUp(x: Float, y: Float) {
        revive()
        speed.set(Random.Float(-8f, +8f), Random.Float(-32f, -48f))
        this.x = x
        this.y = y
        size = 6f
        lifespan = 1f
        left = lifespan
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        // alpha: 0 -> 1 -> 0; size: 6 -> 0; color: 0x660044 -> 0x000000
        color(ColorMath.interpolate(0x000000, 0x440044, p))
        am = if (p < 0.5f) p * p * 4 else (1 - p) * 2
    }
    companion object {
        val MISSILE: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).reset(x, y)
            }
        }
        val CURSE: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetCurse(x, y)
            }
        }
        val UP: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShadowParticle::class.java) as ShadowParticle).resetUp(x, y)
            }
        }
    }
}
