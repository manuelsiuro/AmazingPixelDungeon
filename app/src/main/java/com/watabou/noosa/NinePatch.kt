package com.watabou.noosa
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import java.nio.FloatBuffer
open class NinePatch : Visual {
    var texture: SmartTexture? = null
    protected var vertices: FloatArray
    protected var verticesBuffer: FloatBuffer
    protected var outterF: RectF? = null
    protected var innerF: RectF? = null
    protected var marginLeft: Int = 0
    protected var marginRight: Int = 0
    protected var marginTop: Int = 0
    protected var marginBottom: Int = 0
    protected var nWidth: Float = 0f
    protected var nHeight: Float = 0f
    constructor(tx: Any, margin: Int) : this(tx, margin, margin, margin, margin)
    constructor(tx: Any, left: Int, top: Int, right: Int, bottom: Int) : this(tx, 0, 0, 0, 0, left, top, right, bottom)
    constructor(tx: Any, x: Int, y: Int, w: Int, h: Int, margin: Int) : this(tx, x, y, w, h, margin, margin, margin, margin)
    constructor(tx: Any, x: Int, y: Int, w: Int, h: Int, left: Int, top: Int, right: Int, bottom: Int) : super(0f, 0f, 0f, 0f) {
        texture = TextureCache.get(tx)
        var newWidth = w
        var newHeight = h
        newWidth = if (newWidth == 0) texture!!.width else newWidth
        newHeight = if (newHeight == 0) texture!!.height else newHeight
        this.width = newWidth.toFloat()
        nWidth = width
        this.height = newHeight.toFloat()
        nHeight = height
        vertices = FloatArray(16)
        verticesBuffer = Quad.createSet(9)
        marginLeft = left
        marginRight = right
        marginTop = top
        marginBottom = bottom
        outterF = texture!!.uvRect(x, y, x + newWidth, y + newHeight)
        innerF = texture!!.uvRect(x + left, y + top, x + newWidth - right, y + newHeight - bottom)
        updateVertices()
    }
    protected fun updateVertices() {
        verticesBuffer.position(0)
        val right = width - marginRight
        val bottom = height - marginBottom
        Quad.fill(
            vertices,
            0f, marginLeft.toFloat(), 0f, marginTop.toFloat(), outterF!!.left, innerF!!.left, outterF!!.top, innerF!!.top
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            marginLeft.toFloat(), right, 0f, marginTop.toFloat(), innerF!!.left, innerF!!.right, outterF!!.top, innerF!!.top
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            right, width, 0f, marginTop.toFloat(), innerF!!.right, outterF!!.right, outterF!!.top, innerF!!.top
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            0f, marginLeft.toFloat(), marginTop.toFloat(), bottom, outterF!!.left, innerF!!.left, innerF!!.top, innerF!!.bottom
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            marginLeft.toFloat(), right, marginTop.toFloat(), bottom, innerF!!.left, innerF!!.right, innerF!!.top, innerF!!.bottom
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            right, width, marginTop.toFloat(), bottom, innerF!!.right, outterF!!.right, innerF!!.top, innerF!!.bottom
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            0f, marginLeft.toFloat(), bottom, height, outterF!!.left, innerF!!.left, innerF!!.bottom, outterF!!.bottom
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            marginLeft.toFloat(), right, bottom, height, innerF!!.left, innerF!!.right, innerF!!.bottom, outterF!!.bottom
        )
        verticesBuffer.put(vertices)
        Quad.fill(
            vertices,
            right, width, bottom, height, innerF!!.right, outterF!!.right, innerF!!.bottom, outterF!!.bottom
        )
        verticesBuffer.put(vertices)
    }
    open fun marginLeft(): Int {
        return marginLeft
    }
    open fun marginRight(): Int {
        return marginRight
    }
    open fun marginTop(): Int {
        return marginTop
    }
    open fun marginBottom(): Int {
        return marginBottom
    }
    fun marginHor(): Int {
        return marginLeft + marginRight
    }
    fun marginVer(): Int {
        return marginTop + marginBottom
    }
    fun innerWidth(): Float {
        return width - marginLeft - marginRight
    }
    fun innerHeight(): Float {
        return height - marginTop - marginBottom
    }
    fun innerRight(): Float {
        return width - marginRight
    }
    fun innerBottom(): Float {
        return height - marginBottom
    }
    // size() is method in Java code (no @Override there, but in Resizable it is)
    // NinePatch doesn't implement Resizable in Java code provided
    open fun size(width: Float, height: Float) {
        this.width = width
        this.height = height
        updateVertices()
    }
    override fun draw() {
        super.draw()
        val script = NoosaScript.get()
        texture!!.bind()
        script.camera(camera())
        script.uModel.valueM4(matrix)
        script.lighting(
            rm, gm, bm, am,
            ra, ga, ba, aa
        )
        script.drawQuadSet(verticesBuffer, 9)
    }
}
