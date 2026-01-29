package com.watabou.noosa
import com.watabou.input.Touchscreen
open class TouchArea : Visual, com.watabou.utils.Signal.Listener<Touchscreen.Touch?> {
    // Its target can be toucharea itself
    var target: Visual? = null
    protected var touch: Touchscreen.Touch? = null
    constructor(target: Visual) : super(0f, 0f, 0f, 0f) {
        this.target = target
        Touchscreen.event.add(this)
    }
    constructor(x: Float, y: Float, width: Float, height: Float) : super(x, y, width, height) {
        this.target = this
        visible = false
        Touchscreen.event.add(this)
    }
    override fun onSignal(t: Touchscreen.Touch?) {
        if (!isActive()) {
            return
        }
        val hit = t != null && target!!.overlapsScreenPoint(t.start.x.toInt(), t.start.y.toInt())
        if (hit && t != null) {
            Touchscreen.event.cancel()
            if (t.down) {
                if (this.touch == null) {
                    this.touch = t
                }
                onTouchDown(t)
            } else {
                onTouchUp(t)
                if (this.touch === t) {
                    this.touch = null
                    onClick(t)
                }
            }
        } else {
            if (t == null && this.touch != null) {
                onDrag(this.touch!!)
            } else if (this.touch != null && t != null && !t.down) {
                onTouchUp(t)
                this.touch = null
            }
        }
    }
    protected open fun onTouchDown(touch: Touchscreen.Touch) {}
    protected open fun onTouchUp(touch: Touchscreen.Touch) {}
    protected open fun onClick(touch: Touchscreen.Touch) {}
    protected open fun onDrag(touch: Touchscreen.Touch) {}
    fun reset() {
        touch = null
    }
    override fun destroy() {
        Touchscreen.event.remove(this)
        super.destroy()
    }
}
