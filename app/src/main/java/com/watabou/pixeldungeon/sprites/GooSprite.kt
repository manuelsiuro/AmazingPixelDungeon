package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.PointF
import com.watabou.utils.Random
class GooSprite : MobSprite() {
    private val pump: Animation
    private val jump: Animation
    private var spray: Emitter? = null
    init {
        texture(Assets.GOO)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 20, 14)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 0, 1)
        pump = Animation(20, true)
        pump.frames(frames, 0, 1)
        jump = Animation(1, true)
        jump.frames(frames, 6)
        attack = Animation(10, false)
        attack?.frames(frames, 5, 0, 6)
        die = Animation(10, false)
        die?.frames(frames, 2, 3, 4)
        idle?.let { play(it) }
    }
    fun pumpUp() {
        play(pump)
    }
    override fun play(anim: Animation?, force: Boolean) {
        super.play(anim, force)
        if (anim === pump) {
            spray = centerEmitter()
            spray?.pour(GooParticle.FACTORY, 0.04f)
        } else {
            spray?.let {
                it.on = false
                spray = null
            }
        }
    }
    override fun blood(): Int = 0xFF000000.toInt()
    class GooParticle : PixelParticle.Shrinking() {
        init {
            color(0x000000)
            lifespan = 0.3f
            acc.set(0f, +50f)
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
            size = 4f
            speed.polar(-Random.Float(PointF.PI), Random.Float(32f, 48f))
        }
        override fun update() {
            super.update()
            val p = left / lifespan
            am = if (p > 0.5f) (1 - p) * 2f else 1f
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(GooParticle::class.java) as GooParticle).reset(x, y)
                }
            }
        }
    }
}
