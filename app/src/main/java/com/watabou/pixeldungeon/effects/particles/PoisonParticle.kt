package com.watabou.pixeldungeon.effects.particles
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
class PoisonParticle : PixelParticle() {
    init {
        lifespan = 0.6f
        acc.set(0f, +30f)
    }
    fun resetMissile(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = lifespan
        speed.polar(-Random.Float(3.1415926f), Random.Float(6f))
    }
    fun resetSplash(x: Float, y: Float) {
        revive()
        this.x = x
        this.y = y
        left = lifespan
        speed.polar(Random.Float(3.1415926f), Random.Float(10f, 20f))
    }
    override fun update() {
        super.update()
        // alpha: 1 -> 0; size: 1 -> 4
        left /= lifespan
        am = left
        size(4 - am * 3)
        // Correcting: am = left / lifespan done in one line in java, but here left is modified? 
        // Java: size( 4 - (am = left / lifespan) * 3 );
        // The assignment returns the value. 
        // Wait, left is decremented in super.update().
        // In Java:
        // size( 4 - (am = left / lifespan) * 3 );
        // So am gets (left / lifespan).
        // Then size uses that.
        // Then color uses am.
        // My previous logic:
        // left /= lifespan -> this modifies left! WRONG.
        // It should be:
        am = left / lifespan
        size(4 - am * 3)
        // color: 0x8844FF -> 0x00FF00
        color(ColorMath.interpolate(0x00FF00, 0x8844FF, am))
    }
    companion object {
        val MISSILE: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetMissile(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
        val SPLASH: Emitter.Factory = object : Emitter.Factory() {
            override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                (emitter.recycle(PoisonParticle::class.java) as PoisonParticle).resetSplash(x, y)
            }
            override fun lightMode(): Boolean {
                return true
            }
        }
    }
}
