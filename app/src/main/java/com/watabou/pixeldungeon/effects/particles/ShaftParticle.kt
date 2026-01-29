package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class ShaftParticle : PixelParticle() {
    private var offs: Float = 0f
    init {
        lifespan = 1.2f
        speed.set(0f, -6f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        offs = -Random.Float(lifespan)
        left = lifespan - offs
    }
    override fun update() {
        super.update()
        val p = left / lifespan
        am = if (p < 0.5f) p else 1 - p
        scale.x = (1 - p) * 4
        scale.y = 16 + (1 - p) * 16
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(ShaftParticle::class.java) as ShaftParticle).reset(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
