package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.utils.GameMath
class Banner : Image {
    private enum class State {
        FADE_IN, STATIC, FADE_OUT
    }
    private var state: State? = null
    private var time: Float = 0f
    private var color: Int = 0
    private var fadeTime: Float = 0f
    private var showTime: Float = 0f
    constructor(sample: Image) : super() {
        copy(sample)
        alpha(0f)
    }
    constructor(tx: Any) : super(tx) {
        alpha(0f)
    }
    @JvmOverloads
    fun show(color: Int, fadeTime: Float, showTime: Float = Float.MAX_VALUE) {
        this.color = color
        this.fadeTime = fadeTime
        this.showTime = showTime
        state = State.FADE_IN
        time = fadeTime
    }
    override fun update() {
        super.update()
        time -= Game.elapsed
        if (time >= 0) {
            val p = time / fadeTime
            when (state) {
                State.FADE_IN -> {
                    tint(color, p)
                    alpha(1 - p)
                }
                State.FADE_OUT -> alpha(p)
                else -> {}
            }
        } else {
            when (state) {
                State.FADE_IN -> {
                    time = showTime
                    state = State.STATIC
                }
                State.STATIC -> {
                    time = fadeTime
                    state = State.FADE_OUT
                }
                State.FADE_OUT -> killAndErase()
                else -> {}
            }
        }
    }
}
