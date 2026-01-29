package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
class EarthParticle : PixelParticle() {
    init {
        color(ColorMath.random(0x444444, 0x777766))
        angle = Random.Float(-30f, 30f)
        lifespan = 0.5f
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = lifespan
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        size((if (p < 0.5f) p else 1 - p) * 16)
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(EarthParticle::class.java) as EarthParticle).reset(x, y)
            }
        }
    }
}
