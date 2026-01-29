package com.watabou.noosa.ui
import com.watabou.noosa.Group
open class Component : Group {
    protected var x: Float = 0f
    protected var y: Float = 0f
    protected var width: Float = 0f
    protected var height: Float = 0f
    constructor() : super() {
        createChildren()
    }
    fun setPos(x: Float, y: Float): Component {
        this.x = x
        this.y = y
        layout()
        return this
    }
    fun setSize(width: Float, height: Float): Component {
        this.width = width
        this.height = height
        layout()
        return this
    }
    fun setRect(x: Float, y: Float, width: Float, height: Float): Component {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        layout()
        return this
    }
    fun inside(x: Float, y: Float): Boolean {
        return x >= this.x && y >= this.y && x < this.x + width && y < this.y + height
    }
    fun fill(c: Component) {
        setRect(c.x, c.y, c.width, c.height)
    }
    fun left(): Float {
        return x
    }
    fun right(): Float {
        return x + width
    }
    fun centerX(): Float {
        return x + width / 2
    }
    fun top(): Float {
        return y
    }
    fun bottom(): Float {
        return y + height
    }
    fun centerY(): Float {
        return y + height / 2
    }
    fun width(): Float {
        return width
    }
    fun height(): Float {
        return height
    }
    protected open fun createChildren() {
    }
    protected open fun layout() {
    }
}
