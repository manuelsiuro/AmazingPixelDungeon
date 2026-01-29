package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.Random
class SparkParticle : PixelParticle() {
    init {
        size(2f)
        acc.set(0f, +50f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        lifespan = Random.Float(0.5f, 1.0f)
        left = lifespan
        speed.polar(-Random.Float(3.1415926f), Random.Float(20f, 40f))
    }
    override fun update() {
        super.update()
        size(Random.Float(5 * left / lifespan))
    }
    companion object {
        val FACTORY: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(SparkParticle::class.java) as SparkParticle).reset(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
