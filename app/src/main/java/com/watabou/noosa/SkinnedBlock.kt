package com.watabou.noosa
import android.graphics.RectF
import com.watabou.glwrap.Texture
class SkinnedBlock(width: Float, height: Float, tx: Any) : Image(tx) {
    protected var scaleX: Float = 0f
    protected var scaleY: Float = 0f
    protected var offsetX: Float = 0f
    protected var offsetY: Float = 0f
    var autoAdjust: Boolean = false
    init {
        texture!!.wrap(Texture.REPEAT, Texture.REPEAT)
        size(width, height)
    }
    override fun frame(frame: RectF) {
        scaleX = 1f
        scaleY = 1f
        offsetX = 0f
        offsetY = 0f
        super.frame(RectF(0f, 0f, 1f, 1f))
    }
    override fun updateFrame() {
        if (autoAdjust) {
            while (offsetX > texture!!.width) {
                offsetX -= texture!!.width.toFloat()
            }
            while (offsetX < -texture!!.width) {
                offsetX += texture!!.width.toFloat()
            }
            while (offsetY > texture!!.height) {
                offsetY -= texture!!.height.toFloat()
            }
            while (offsetY < -texture!!.height) {
                offsetY += texture!!.height.toFloat()
            }
        }
        val tw = 1f / texture!!.width
        val th = 1f / texture!!.height
        val u0 = offsetX * tw
        val v0 = offsetY * th
        val u1 = u0 + width * tw / scaleX
        val v1 = v0 + height * th / scaleY
        vertices[2] = u0
        vertices[3] = v0
        vertices[6] = u1
        vertices[7] = v0
        vertices[10] = u1
        vertices[11] = v1
        vertices[14] = u0
        vertices[15] = v1
        dirty = true
    }
    fun offsetTo(x: Float, y: Float) {
        offsetX = x
        offsetY = y
        updateFrame()
    }
    fun offset(x: Float, y: Float) {
        offsetX += x
        offsetY += y
        updateFrame()
    }
    fun offsetX(): Float {
        return offsetX
    }
    fun offsetY(): Float {
        return offsetY
    }
    fun scale(x: Float, y: Float) {
        scaleX = x
        scaleY = y
        updateFrame()
    }
    fun size(w: Float, h: Float) {
        this.width = w
        this.height = h
        updateFrame()
        updateVertices()
    }
}
