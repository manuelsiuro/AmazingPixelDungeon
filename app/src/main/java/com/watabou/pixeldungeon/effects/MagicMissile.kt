package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.effects.particles.*
import com.watabou.utils.Callback
import com.watabou.utils.ColorMath
import com.watabou.utils.PointF
import com.watabou.utils.Random
open class MagicMissile : Emitter() {
    private var callback: Callback? = null
    private var sx: Float = 0f
    private var sy: Float = 0f
    private var missileTime: Float = 0f
    fun reset(from: Int, to: Int, callback: Callback?) {
        reset(from, to, SPEED, callback)
    }
    fun reset(from: Int, to: Int, velocity: Float, callback: Callback?) {
        this.callback = callback
        revive()
        val pf = DungeonTilemap.tileCenterToWorld(from)
        val pt = DungeonTilemap.tileCenterToWorld(to)
        x = pf.x
        y = pf.y
        width = 0f
        height = 0f
        val d = PointF.diff(pt, pf)
        val speed = PointF(d).normalize().scale(velocity)
        sx = speed.x
        sy = speed.y
        missileTime = d.length() / velocity
    }
    fun size(size: Float) {
        x -= size / 2
        y -= size / 2
        width = size
        height = size
    }
    override fun update() {
        super.update()
        if (on) {
            val d = Game.elapsed
            x += sx * d
            y += sy * d
            missileTime -= d
            if (missileTime <= 0) {
                on = false
                callback?.call()
            }
        }
    }
    class MagicParticle : PixelParticle() {
        init {
            color(0x88CCFF)
            lifespan = 0.5f
            speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
        }
        override fun update() {
            super.update()
            // alpha: 1 -> 0; size: 1 -> 4
            size(4 - (left / lifespan).also { am = it } * 3)
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(MagicParticle::class.java) as MagicParticle).reset(x, y)
                }
                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }
    class EarthParticle : PixelParticle.Shrinking() {
        init {
            lifespan = 0.5f
            color(ColorMath.random(0x555555, 0x777766))
            acc.set(0f, +40f)
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
            size = 4f
            speed.set(Random.Float(-10f, +10f), Random.Float(-10f, +10f))
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(EarthParticle::class.java) as EarthParticle).reset(x, y)
                }
            }
        }
    }
    class WhiteParticle : PixelParticle() {
        init {
            lifespan = 0.4f
            am = 0.5f
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
        }
        override fun update() {
            super.update()
            // size: 3 -> 0
            size((left / lifespan) * 3)
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(WhiteParticle::class.java) as WhiteParticle).reset(x, y)
                }
                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }
    class SlowParticle : PixelParticle() {
        private lateinit var emitter: Emitter
        init {
            lifespan = 0.6f
            color(0x664422)
            size(2f)
        }
        fun reset(x: Float, y: Float, emitter: Emitter) {
            revive()
            this.x = x
            this.y = y
            this.emitter = emitter
            left = lifespan
            acc.set(0f)
            speed.set(Random.Float(-20f, +20f), Random.Float(-20f, +20f))
        }
        override fun update() {
            super.update()
            am = left / lifespan
            acc.set((emitter.x - x) * 10, (emitter.y - y) * 10)
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(SlowParticle::class.java) as SlowParticle).reset(x, y, emitter)
                }
                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }
    class ForceParticle : PixelParticle.Shrinking() {
        fun reset(index: Int, x: Float, y: Float) {
            super.reset(x, y, 0xFFFFFF, 8f, 0.5f)
            speed.polar((PointF.PI2 / 8f) * index, 12f)
            this.x -= speed.x * lifespan
            this.y -= speed.y * lifespan
        }
        override fun update() {
            super.update()
            am = (1 - left / lifespan) / 2
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ForceParticle::class.java) as ForceParticle).reset(index, x, y)
                }
            }
        }
    }
    class ColdParticle : PixelParticle.Shrinking() {
        init {
            lifespan = 0.6f
            color(0x2244FF)
        }
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            left = lifespan
            size = 8f
        }
        override fun update() {
            super.update()
            am = 1 - left / lifespan
        }
        companion object {
            val FACTORY: Emitter.Factory = object : Emitter.Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    (emitter.recycle(ColdParticle::class.java) as ColdParticle).reset(x, y)
                }
                override fun lightMode(): Boolean {
                    return true
                }
            }
        }
    }
    companion object {
        private const val SPEED = 200f
        fun blueLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.pour(MagicParticle.FACTORY, 0.01f)
        }
        fun fire(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(FlameParticle.FACTORY, 0.01f)
        }
        fun earth(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(2f)
            missile.pour(EarthParticle.FACTORY, 0.01f)
        }
        fun purpleLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(2f)
            missile.pour(PurpleParticle.MISSILE, 0.01f)
        }
        fun whiteLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(WhiteParticle.FACTORY, 0.01f)
        }
        fun wool(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(3f)
            missile.pour(WoolParticle.FACTORY, 0.01f)
        }
        fun poison(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(3f)
            missile.pour(PoisonParticle.MISSILE, 0.01f)
        }
        fun foliage(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(LeafParticle.GENERAL, 0.01f)
        }
        fun slowness(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.pour(SlowParticle.FACTORY, 0.01f)
        }
        fun force(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(0f)
            missile.pour(ForceParticle.FACTORY, 0.01f)
        }
        fun coldLight(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(ColdParticle.FACTORY, 0.01f)
        }
        fun shadow(group: Group, from: Int, to: Int, callback: Callback?) {
            val missile = group.recycle(MagicMissile::class.java) as MagicMissile
            missile.reset(from, to, callback)
            missile.size(4f)
            missile.pour(ShadowParticle.MISSILE, 0.01f)
        }
    }
}
