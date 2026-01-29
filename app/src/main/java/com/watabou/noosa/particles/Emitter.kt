package com.watabou.noosa.particles
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Visual
import com.watabou.utils.PointF
import com.watabou.utils.Random
import javax.microedition.khronos.opengles.GL10
open class Emitter : Group {
    protected var lightMode: Boolean = false
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f
    protected var target: Visual? = null
    protected var interval: Float = 0f
    protected var quantity: Int = 0
    var on: Boolean = false
    var autoKill: Boolean = true
    protected var count: Int = 0
    protected var time: Float = 0f
    protected var factory: Factory? = null
    constructor() : super()
    fun pos(x: Float, y: Float) {
        pos(x, y, 0f, 0f)
    }
    fun pos(p: PointF) {
        pos(p.x, p.y, 0f, 0f)
    }
    open fun pos(x: Float, y: Float, width: Float, height: Float) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
        target = null
    }
    fun pos(target: Visual) {
        this.target = target
    }
    fun burst(factory: Factory, quantity: Int) {
        start(factory, 0f, quantity)
    }
    fun pour(factory: Factory, interval: Float) {
        start(factory, interval, 0)
    }
    fun start(factory: Factory, interval: Float, quantity: Int) {
        this.factory = factory
        this.lightMode = factory.lightMode()
        this.interval = interval
        this.quantity = quantity
        count = 0
        time = Random.Float(interval)
        on = true
    }
    override fun update() {
        if (on) {
            time += Game.elapsed
            while (time > interval) {
                time -= interval
                emit(count++)
                if (quantity > 0 && count >= quantity) {
                    on = false
                    break
                }
            }
        } else if (autoKill && countLiving() == 0) {
            kill()
        }
        super.update()
    }
    protected open fun emit(index: Int) {
        if (target == null) {
            factory!!.emit(
                this,
                index,
                x + Random.Float(width),
                y + Random.Float(height)
            )
        } else {
            factory!!.emit(
                this,
                index,
                target!!.x + Random.Float(target!!.width),
                target!!.y + Random.Float(target!!.height)
            )
        }
    }
    override fun draw() {
        if (lightMode) {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            super.draw()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            super.draw()
        }
    }
    abstract class Factory {
        abstract fun emit(emitter: Emitter, index: Int, x: Float, y: Float)
        open fun lightMode(): Boolean {
            return false
        }
    }
}
