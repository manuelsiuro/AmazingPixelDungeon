package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.PointF
class BadgeBanner private constructor(private val index: Int) : Image(Assets.BADGES) {
    private enum class State {
        FADE_IN, STATIC, FADE_OUT
    }
    private var state: State
    private var time: Float
    init {
        if (atlas == null) {
            atlas = TextureFilm(requireNotNull(texture) { "BadgeBanner texture must not be null" }, 16, 16)
        }
        frame(requireNotNull(requireNotNull(atlas).get(index)))
        origin.set(width / 2, height / 2)
        alpha(0f)
        scale.set(2 * DEFAULT_SCALE)
        state = State.FADE_IN
        time = FADE_IN_TIME
        Sample.play(Assets.SND_BADGE)
    }
    override fun update() {
        super.update()
        time -= Game.elapsed
        if (time >= 0) {
            when (state) {
                State.FADE_IN -> {
                    val p = time / FADE_IN_TIME
                    scale.set((1 + p) * DEFAULT_SCALE)
                    alpha(1 - p)
                }
                State.STATIC -> {
                }
                State.FADE_OUT -> alpha(time / FADE_OUT_TIME)
            }
        } else {
            when (state) {
                State.FADE_IN -> {
                    time = STATIC_TIME
                    state = State.STATIC
                    scale.set(DEFAULT_SCALE)
                    alpha(1f)
                    highlight(this, index)
                }
                State.STATIC -> {
                    time = FADE_OUT_TIME
                    state = State.FADE_OUT
                }
                State.FADE_OUT -> killAndErase()
            }
        }
    }
    override fun kill() {
        if (current === this) {
            current = null
        }
        super.kill()
    }
    companion object {
        private const val DEFAULT_SCALE = 3f
        private const val FADE_IN_TIME = 0.2f
        private const val STATIC_TIME = 1f
        private const val FADE_OUT_TIME = 1.0f
        private var atlas: TextureFilm? = null
        private var current: BadgeBanner? = null
        fun highlight(image: Image, index: Int) {
            val p = PointF()
            when (index) {
                0, 1, 2, 3 -> p.offset(7f, 3f)
                4, 5, 6, 7 -> p.offset(6f, 5f)
                8, 9, 10, 11 -> p.offset(6f, 3f)
                12, 13, 14, 15 -> p.offset(7f, 4f)
                16 -> p.offset(6f, 3f)
                17 -> p.offset(5f, 4f)
                18 -> p.offset(7f, 3f)
                20 -> p.offset(7f, 3f)
                21 -> p.offset(7f, 3f)
                22 -> p.offset(6f, 4f)
                23 -> p.offset(4f, 5f)
                24 -> p.offset(6f, 4f)
                25 -> p.offset(6f, 5f)
                26 -> p.offset(5f, 5f)
                27 -> p.offset(6f, 4f)
                28 -> p.offset(3f, 5f)
                29 -> p.offset(5f, 4f)
                30 -> p.offset(5f, 4f)
                31 -> p.offset(5f, 5f)
                32, 33 -> p.offset(7f, 4f)
                34 -> p.offset(6f, 4f)
                35 -> p.offset(6f, 4f)
                36 -> p.offset(6f, 5f)
                37 -> p.offset(4f, 4f)
                38 -> p.offset(5f, 5f)
                39 -> p.offset(5f, 4f)
                40, 41, 42, 43 -> p.offset(5f, 4f)
                44, 45, 46, 47 -> p.offset(5f, 5f)
                48, 49, 50, 51 -> p.offset(7f, 4f)
                52, 53, 54, 55 -> p.offset(4f, 4f)
                56 -> p.offset(3f, 7f)
                57 -> p.offset(4f, 5f)
                58 -> p.offset(6f, 4f)
                59 -> p.offset(7f, 4f)
                60, 61, 62, 63 -> p.offset(4f, 4f)
                64, 65, 66, 67 -> p.offset(5f, 4f)
            }
            p.x *= image.scale.x
            p.y *= image.scale.y
            p.offset(
                -image.origin.x * (image.scale.x - 1),
                -image.origin.y * (image.scale.y - 1)
            )
            p.offset(image.point())
            val star = Speck()
            star.reset(0, p.x, p.y, Speck.DISCOVER)
            star.camera = image.camera()
            image.parent?.add(star)
        }
        fun show(image: Int): BadgeBanner {
            current?.killAndErase()
            val banner = BadgeBanner(image)
            current = banner
            return banner
        }
        fun image(index: Int): Image {
            val image = Image(Assets.BADGES)
            if (atlas == null) {
                atlas = TextureFilm(requireNotNull(image.texture) { "Badge image texture must not be null" }, 16, 16)
            }
            image.frame(requireNotNull(requireNotNull(atlas).get(index)))
            return image
        }
    }
}
