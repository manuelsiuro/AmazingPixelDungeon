package com.watabou.pixeldungeon.effects
import android.annotation.SuppressLint
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.PointF
import com.watabou.utils.Random
import com.watabou.utils.SparseArray
class Speck : Image() {
    private var type: Int = 0
    private var lifespan: Float = 0f
    private var left: Float = 0f
    init {
        texture(Assets.SPECKS)
        if (film == null) {
            film = TextureFilm(requireNotNull(texture) { "Speck texture must not be null" }, SIZE, SIZE)
        }
        origin.set(SIZE / 2f)
    }
    fun reset(index: Int, x: Float, y: Float, type: Int) {
        revive()
        val f = requireNotNull(film) { "Speck film must not be null" }
        this.type = type
        when (type) {
            DISCOVER -> frame(requireNotNull(f.get(LIGHT)))
            EVOKE, MASTERY, KIT, FORGE -> frame(requireNotNull(f.get(STAR)))
            RATTLE -> frame(requireNotNull(f.get(BONE)))
            JET, TOXIC, PARALYSIS, CONFUSION, DUST -> frame(requireNotNull(f.get(STEAM)))
            else -> frame(requireNotNull(f.get(type)))
        }
        this.x = x - origin.x
        this.y = y - origin.y
        resetColor()
        scale.set(1f)
        speed.set(0f)
        acc.set(0f)
        angle = 0f
        angularSpeed = 0f
        when (type) {
            HEALING -> {
                speed.set(0f, -20f)
                lifespan = 1f
            }
            STAR -> {
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(128f))
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                lifespan = 1f
            }
            FORGE -> {
                speed.polar(-Random.Float(3.1415926f), Random.Float(64f))
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                lifespan = 0.51f
            }
            EVOKE -> {
                speed.polar(-Random.Float(3.1415926f), 50f)
                acc.set(0f, 50f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-180f, +180f)
                lifespan = 1f
            }
            KIT -> {
                speed.polar(index * 3.1415926f / 5, 50f)
                acc.set(-speed.x, -speed.y)
                angle = index * 36f
                angularSpeed = 360f
                lifespan = 1f
            }
            MASTERY -> {
                speed.set(if (Random.Int(2) == 0) Random.Float(-128f, -64f) else Random.Float(+64f, +128f), 0f)
                angularSpeed = if (speed.x < 0) -180f else +180f
                acc.set(-speed.x, 0f)
                lifespan = 0.5f
            }
            LIGHT -> {
                angle = Random.Float(360f)
                angularSpeed = 90f
                lifespan = 1f
            }
            DISCOVER -> {
                angle = Random.Float(360f)
                angularSpeed = 90f
                lifespan = 0.5f
                am = 0f
            }
            QUESTION -> lifespan = 0.8f
            UP -> {
                speed.set(0f, -20f)
                lifespan = 1f
            }
            SCREAM -> lifespan = 0.9f
            BONE -> {
                lifespan = 0.2f
                speed.polar(Random.Float(2 * 3.1415926f), 24 / lifespan)
                acc.set(0f, 128f)
                angle = Random.Float(360f)
                angularSpeed = 360f
            }
            RATTLE -> {
                lifespan = 0.5f
                speed.set(0f, -200f)
                acc.set(0f, -2 * speed.y / lifespan)
                angle = Random.Float(360f)
                angularSpeed = 360f
            }
            WOOL -> {
                lifespan = 0.5f
                speed.set(0f, -50f)
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
            }
            ROCK -> {
                angle = Random.Float(360f)
                angularSpeed = Random.Float(-360f, +360f)
                scale.set(Random.Float(1f, 2f))
                speed.set(0f, 64f)
                lifespan = 0.2f
                this.y -= speed.y * lifespan
            }
            NOTE -> {
                angularSpeed = Random.Float(-30f, +30f)
                speed.polar((angularSpeed - 90) * PointF.G2R, 30f)
                lifespan = 1f
            }
            CHANGE -> {
                angle = Random.Float(360f)
                speed.polar((angle - 90) * PointF.G2R, Random.Float(4f, 12f))
                lifespan = 1.5f
            }
            HEART -> {
                speed.set(Random.Int(-10, +10).toFloat(), -40f)
                angularSpeed = Random.Float(-45f, +45f)
                lifespan = 1f
            }
            BUBBLE -> {
                speed.set(0f, -15f)
                scale.set(Random.Float(0.8f, 1f))
                lifespan = Random.Float(0.8f, 1.5f)
            }
            STEAM -> {
                speed.y = -Random.Float(20f, 30f)
                angularSpeed = Random.Float(+180f)
                angle = Random.Float(360f)
                lifespan = 1f
            }
            JET -> {
                speed.y = +32f
                acc.y = -64f
                angularSpeed = Random.Float(180f, 360f)
                angle = Random.Float(360f)
                lifespan = 0.5f
            }
            TOXIC -> {
                hardlight(0x50FF60)
                angularSpeed = 30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }
            PARALYSIS -> {
                hardlight(0xFFFF66)
                angularSpeed = -30f
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }
            CONFUSION -> {
                hardlight(Random.Int(0x1000000) or 0x000080)
                angularSpeed = Random.Float(-20f, +20f)
                angle = Random.Float(360f)
                lifespan = Random.Float(1f, 3f)
            }
            DUST -> {
                hardlight(0xFFFF66)
                angle = Random.Float(360f)
                speed.polar(Random.Float(2 * 3.1415926f), Random.Float(16f, 48f))
                lifespan = 0.5f
            }
            COIN -> {
                speed.polar(-PointF.PI * Random.Float(0.3f, 0.7f), Random.Float(48f, 96f))
                acc.y = 256f
                lifespan = -speed.y / acc.y * 2
            }
        }
        left = lifespan
    }
    @SuppressLint("FloatMath")
    override fun update() {
        super.update()
        left -= Game.elapsed
        if (left <= 0) {
            kill()
        } else {
            val p = 1 - left / lifespan // 0 -> 1
            when (type) {
                STAR, FORGE -> {
                    scale.set(1 - p)
                    am = if (p < 0.2f) p * 5f else (1 - p) * 1.25f
                }
                KIT, MASTERY -> am = 1 - p * p
                EVOKE, HEALING -> am = if (p < 0.5f) 1f else 2 - p * 2
                LIGHT -> {
                    am = scale.set(if (p < 0.2f) p * 5f else (1 - p) * 1.25f).x
                }
                DISCOVER -> {
                    am = 1 - p
                    scale.set((if (p < 0.5f) p else 1 - p) * 2)
                }
                QUESTION -> scale.set(Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()).toFloat() * 3)
                UP -> scale.set(Math.sqrt((if (p < 0.5f) p else 1 - p).toDouble()).toFloat() * 2)
                SCREAM -> {
                    am = Math.sqrt((if (p < 0.5f) p else 1 - p) * 2f.toDouble()).toFloat()
                    scale.set(p * 7)
                }
                BONE, RATTLE -> am = if (p < 0.9f) 1f else (1 - p) * 10
                ROCK -> am = if (p < 0.2f) p * 5 else 1f
                NOTE -> am = 1 - p * p
                WOOL -> scale.set(1 - p)
                CHANGE -> {
                    am = Math.sqrt((if (p < 0.5f) p else 1 - p) * 2.0).toFloat()
                    scale.y = (1 + p) * 0.5f
                    scale.x = (scale.y * Math.cos(left * 15.0)).toFloat()
                }
                HEART -> {
                    scale.set(1 - p)
                    am = 1 - p * p
                }
                BUBBLE -> am = if (p < 0.2f) p * 5 else 1f
                STEAM, TOXIC, PARALYSIS, CONFUSION, DUST -> {
                    am = if (p < 0.5f) p else 1 - p
                    scale.set(1 + p * 2)
                }
                JET -> {
                    am = (if (p < 0.5f) p else 1 - p) * 2
                    scale.set(p * 1.5f)
                }
                COIN -> {
                    scale.x = Math.cos(left * 5.0).toFloat()
                    rm = Math.abs(scale.x) + 1
                    gm = rm
                    bm = gm * 0.5f
                    am = if (p < 0.9f) 1f else (1 - p) * 10
                }
            }
        }
    }
    companion object {
        const val HEALING = 0
        const val STAR = 1
        const val LIGHT = 2
        const val QUESTION = 3
        const val UP = 4
        const val SCREAM = 5
        const val BONE = 6
        const val WOOL = 7
        const val ROCK = 8
        const val NOTE = 9
        const val CHANGE = 10
        const val HEART = 11
        const val BUBBLE = 12
        const val STEAM = 13
        const val COIN = 14
        const val DISCOVER = 101
        const val EVOKE = 102
        const val MASTERY = 103
        const val KIT = 104
        const val RATTLE = 105
        const val JET = 106
        const val TOXIC = 107
        const val PARALYSIS = 108
        const val DUST = 109
        const val FORGE = 110
        const val CONFUSION = 111
        private const val SIZE = 7
        private var film: TextureFilm? = null
        private val factories = SparseArray<Emitter.Factory>()
        fun factory(type: Int): Emitter.Factory {
            return factory(type, false)
        }
        fun factory(type: Int, lightMode: Boolean): Emitter.Factory {
            var factory = factories.get(type)
            if (factory == null) {
                factory = object : Emitter.Factory() {
                    override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                        val p = emitter.recycle(Speck::class.java) as Speck
                        p.reset(index, x, y, type)
                    }
                    override fun lightMode(): Boolean {
                        return lightMode
                    }
                }
                factories.put(type, factory)
            }
            return factory
        }
    }
}
