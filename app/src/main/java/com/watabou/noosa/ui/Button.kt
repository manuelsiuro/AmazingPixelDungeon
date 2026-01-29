package com.watabou.noosa.ui
import com.watabou.input.Touchscreen
import com.watabou.noosa.Game
import com.watabou.noosa.TouchArea
open class Button : Component() {
    protected var hotArea: TouchArea? = null
    protected var pressed: Boolean = false
    protected var pressTime: Float = 0f
    protected var processed: Boolean = false
    override fun createChildren() {
        hotArea = object : TouchArea(0f, 0f, 0f, 0f) {
            override fun onTouchDown(touch: Touchscreen.Touch) {
                pressed = true
                pressTime = 0f
                processed = false
                this@Button.onTouchDown()
            }
            override fun onTouchUp(touch: Touchscreen.Touch) {
                pressed = false
                this@Button.onTouchUp()
            }
            override fun onClick(touch: Touchscreen.Touch) {
                if (!processed) {
                    this@Button.onClick()
                }
            }
        }
        add(hotArea!!)
    }
    override fun update() {
        super.update()
        hotArea?.active = isVisible()
        if (pressed) {
            pressTime += Game.elapsed
            if (pressTime >= longClick) {
                pressed = false
                if (onLongClick()) {
                    hotArea?.reset()
                    processed = true
                    onTouchUp()
                    Game.vibrate(50)
                }
            }
        }
    }
    protected open fun onTouchDown() {}
    protected open fun onTouchUp() {}
    protected open fun onClick() {}
    protected open fun onLongClick(): Boolean {
        return false
    }
    override fun layout() {
        if (hotArea != null) {
            hotArea!!.x = x
            hotArea!!.y = y
            hotArea!!.width = width
            hotArea!!.height = height
        }
    }
    companion object {
        var longClick: Float = 1f
    }
}
