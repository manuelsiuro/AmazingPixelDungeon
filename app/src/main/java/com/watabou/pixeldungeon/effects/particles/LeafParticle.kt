package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Dungeon
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
class LeafParticle : PixelParticle.Shrinking() {
    init {
        lifespan = 1.2f
        acc.set(0f, 25f)
    }
    fun reset(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        speed.set(Random.Float(-8f, +8f), -20f)
        left = lifespan
        size = Random.Float(2f, 3f)
    }
    companion object {
        var color1: Int = 0
        var color2: Int = 0
        val GENERAL: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = (emitter.recycle(LeafParticle::class.java) as LeafParticle)
                p.color(ColorMath.random(0x004400, 0x88CC44))
                p.reset(x, y)
            }
        }
        val LEVEL_SPECIFIC: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                val p = (emitter.recycle(LeafParticle::class.java) as LeafParticle)
                val level = Dungeon.level ?: return
                p.color(ColorMath.random(level.color1, level.color2))
                p.reset(x, y)
            }
        }
    }
}
