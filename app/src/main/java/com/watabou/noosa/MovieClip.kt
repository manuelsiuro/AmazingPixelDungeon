package com.watabou.noosa
import android.graphics.RectF
import java.util.ArrayList
open class MovieClip : Image {
    protected var curAnim: Animation? = null
    protected var curFrame: Int = 0
    protected var frameTimer: Float = 0f
    protected var finished: Boolean = false
    var paused: Boolean = false
    var listener: Listener? = null
    constructor() : super()
    constructor(tx: Any) : super(tx)
    override fun update() {
        super.update()
        if (!paused) {
            updateAnimation()
        }
    }
    protected fun updateAnimation() {
        val anim = curAnim
        if (anim != null && anim.delay > 0 && (anim.looped || !finished)) {
            val lastFrame = curFrame
            frameTimer += Game.elapsed
            while (frameTimer > anim.delay) {
                frameTimer -= anim.delay
                if (curFrame == anim.frames!!.size - 1) {
                    if (anim.looped) {
                        curFrame = 0
                    }
                    finished = true
                    val l = listener
                    if (l != null) {
                        l.onComplete(anim)
                        // This check can probably be removed
                        if (curAnim == null) {
                            return
                        }
                    }
                } else {
                    curFrame++
                }
            }
            if (curFrame != lastFrame) {
                frame(anim.frames!![curFrame])
            }
        }
    }
    fun play(anim: Animation) {
        play(anim, false)
    }
    open fun play(anim: Animation?, force: Boolean) {
        if (!force && (curAnim != null) && (curAnim === anim) && (curAnim!!.looped || !finished)) {
            return
        }
        curAnim = anim
        curFrame = 0
        finished = false
        frameTimer = 0f
        if (anim != null) {
            frame(anim.frames!![curFrame])
        }
    }
    class Animation {
        var delay: Float = 0f
        var frames: Array<RectF>? = null
        var looped: Boolean = false
        constructor(fps: Int, looped: Boolean) {
            this.delay = 1f / fps
            this.looped = looped
        }
        fun frames(vararg frames: RectF): Animation {
            @Suppress("UNCHECKED_CAST")
            this.frames = frames as Array<RectF>
            return this
        }
        fun frames(film: TextureFilm, vararg frames: Any): Animation {
            this.frames = Array(frames.size) { i -> film[frames[i]]!! }
            return this
        }
        public fun clone(): Animation {
            val clone = Animation(Math.round(1 / delay), looped)
            clone.frames = frames
            return clone
        }
    }
    interface Listener {
        fun onComplete(anim: Animation)
    }
}
