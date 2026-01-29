package com.watabou.noosa
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.glwrap.Quad
import java.nio.FloatBuffer
open class Image : Visual {
    var texture: SmartTexture? = null
    protected var frame: RectF? = null
    var flipHorizontal: Boolean = false
    var flipVertical: Boolean = false
    protected var vertices: FloatArray
    protected var verticesBuffer: FloatBuffer
    protected var dirty: Boolean = false
    constructor() : super(0f, 0f, 0f, 0f) {
        vertices = FloatArray(16)
        verticesBuffer = Quad.create()
    }
    constructor(src: Image) : this() {
        copy(src)
    }
    constructor(tx: Any) : this() {
        texture(tx)
    }
    constructor(tx: Any, left: Int, top: Int, width: Int, height: Int) : this(tx) {
        frame(texture!!.uvRect(left, top, left + width, top + height))
    }
    fun texture(tx: Any) {
        texture = if (tx is SmartTexture) tx else TextureCache.get(tx)
        frame(RectF(0f, 0f, 1f, 1f))
    }
    open fun frame(frame: RectF) {
        this.frame = frame
        width = frame.width() * texture!!.width
        height = frame.height() * texture!!.height
        updateFrame()
        updateVertices()
    }
    fun frame(left: Int, top: Int, width: Int, height: Int) {
        frame(texture!!.uvRect(left, top, left + width, top + height))
    }
    fun frame(): RectF {
        return RectF(frame)
    }
    fun copy(other: Image) {
        texture = other.texture
        frame = RectF(other.frame)
        width = other.width
        height = other.height
        updateFrame()
        updateVertices()
    }
    protected open fun updateFrame() {
        if (flipHorizontal) {
            vertices[2] = frame!!.right
            vertices[6] = frame!!.left
            vertices[10] = frame!!.left
            vertices[14] = frame!!.right
        } else {
            vertices[2] = frame!!.left
            vertices[6] = frame!!.right
            vertices[10] = frame!!.right
            vertices[14] = frame!!.left
        }
        if (flipVertical) {
            vertices[3] = frame!!.bottom
            vertices[7] = frame!!.bottom
            vertices[11] = frame!!.top
            vertices[15] = frame!!.top
        } else {
            vertices[3] = frame!!.top
            vertices[7] = frame!!.top
            vertices[11] = frame!!.bottom
            vertices[15] = frame!!.bottom
        }
        dirty = true
    }
    protected fun updateVertices() {
        vertices[0] = 0f
        vertices[1] = 0f
        vertices[4] = width
        vertices[5] = 0f
        vertices[8] = width
        vertices[9] = height
        vertices[12] = 0f
        vertices[13] = height
        dirty = true
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
        if (dirty) {
            verticesBuffer.position(0)
            verticesBuffer.put(vertices)
            dirty = false
        }
        script.drawQuad(verticesBuffer)
    }
}
