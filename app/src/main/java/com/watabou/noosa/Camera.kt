package com.watabou.noosa
import com.watabou.glwrap.Matrix
import com.watabou.utils.Point
import com.watabou.utils.PointF
import com.watabou.utils.Random
import java.util.ArrayList
open class Camera(
    var x: Int,
    var y: Int,
    var width: Int,
    var height: Int,
    var zoom: Float
) : Gizmo() {
    var screenWidth: Int = (width * zoom).toInt()
    var screenHeight: Int = (height * zoom).toInt()
    var matrix: FloatArray = FloatArray(16)
    var scroll: PointF = PointF()
    var target: Visual? = null
    private var shakeMagX = 10f
    private var shakeMagY = 10f
    private var shakeTime = 0f
    private var shakeDuration = 1f
    protected var shakeX: Float = 0f
    protected var shakeY: Float = 0f
    init {
        Matrix.setIdentity(matrix)
    }
    override fun destroy() {
        target = null
        // matrix = null // Cannot nullify array in Kotlin if defined as non-null
    }
    fun zoom(value: Float) {
        zoom(
            value,
            scroll.x + width / 2,
            scroll.y + height / 2
        )
    }
    fun zoom(value: Float, fx: Float, fy: Float) {
        zoom = value
        width = (screenWidth / zoom).toInt()
        height = (screenHeight / zoom).toInt()
        focusOn(fx, fy)
    }
    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        screenWidth = (width * zoom).toInt()
        screenHeight = (height * zoom).toInt()
    }
    override fun update() {
        super.update()
        if (target != null) {
            focusOn(target!!)
        }
        shakeTime -= Game.elapsed
        if (shakeTime > 0) {
            val damping = shakeTime / shakeDuration
            shakeX = Random.Float(-shakeMagX, +shakeMagX) * damping
            shakeY = Random.Float(-shakeMagY, +shakeMagY) * damping
        } else {
            shakeX = 0f
            shakeY = 0f
        }
        updateMatrix()
    }
    fun center(): PointF {
        return PointF((width / 2).toFloat(), (height / 2).toFloat())
    }
    fun hitTest(x: Float, y: Float): Boolean {
        return x >= this.x && y >= this.y && x < this.x + screenWidth && y < this.y + screenHeight
    }
    fun focusOn(x: Float, y: Float) {
        scroll.set(x - width / 2, y - height / 2)
    }
    fun focusOn(point: PointF) {
        focusOn(point.x, point.y)
    }
    fun focusOn(visual: Visual) {
        focusOn(visual.center())
    }
    fun screenToCamera(x: Int, y: Int): PointF {
        return PointF(
            (x - this.x) / zoom + scroll.x,
            (y - this.y) / zoom + scroll.y
        )
    }
    fun cameraToScreen(x: Float, y: Float): Point {
        return Point(
            ((x - scroll.x) * zoom + this.x).toInt(),
            ((y - scroll.y) * zoom + this.y).toInt()
        )
    }
    fun screenWidth(): Float {
        return width * zoom
    }
    fun screenHeight(): Float {
        return height * zoom
    }
    protected open fun updateMatrix() {
        /*	Matrix.setIdentity( matrix );
            Matrix.translate( matrix, -1, +1 );
            Matrix.scale( matrix, 2f / G.width, -2f / G.height );
            Matrix.translate( matrix, x, y );
            Matrix.scale( matrix, zoom, zoom );
            Matrix.translate( matrix, scroll.x, scroll.y );*/
        matrix[0] = +zoom * invW2
        matrix[5] = -zoom * invH2
        matrix[12] = -1 + x * invW2 - (scroll.x + shakeX) * matrix[0]
        matrix[13] = +1 - y * invH2 - (scroll.y + shakeY) * matrix[5]
    }
    fun shake(magnitude: Float, duration: Float) {
        shakeMagY = magnitude
        shakeMagX = shakeMagY
        shakeDuration = duration
        shakeTime = shakeDuration
    }
    companion object {
        var all: ArrayList<Camera> = ArrayList()
        var invW2: Float = 0f
        var invH2: Float = 0f
        var main: Camera? = null
        fun reset(): Camera {
            return reset(createFullscreen(1f))
        }
        fun reset(newCamera: Camera): Camera {
            invW2 = 2f / Game.width
            invH2 = 2f / Game.height
            val length = all.size
            for (i in 0 until length) {
                all[i].destroy()
            }
            all.clear()
            add(newCamera)
            main = newCamera
            return newCamera
        }
        fun add(camera: Camera): Camera {
            all.add(camera)
            return camera
        }
        fun remove(camera: Camera): Camera {
            all.remove(camera)
            return camera
        }
        fun updateAll() {
            val length = all.size
            for (i in 0 until length) {
                val c = all[i]
                if (c.exists && c.active) {
                    c.update()
                }
            }
        }
        fun createFullscreen(zoom: Float): Camera {
            val w = Math.ceil((Game.width / zoom).toDouble()).toInt()
            val h = Math.ceil((Game.height / zoom).toDouble()).toInt()
            return Camera(
                ((Game.width - w * zoom) / 2).toInt(),
                ((Game.height - h * zoom) / 2).toInt(),
                w, h, zoom
            )
        }
    }
}
