package com.watabou.glwrap
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
open class Texture {
    var id: Int = 0
    var premultiplied: Boolean = false
    init {
        val ids = IntArray(1)
        GLES20.glGenTextures(1, ids, 0)
        id = ids[0]
        bind()
    }
    fun bind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id)
    }
    open fun filter(minMode: Int, maxMode: Int) {
        bind()
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minMode.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, maxMode.toFloat())
    }
    open fun wrap(s: Int, t: Int) {
        bind()
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, s.toFloat())
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, t.toFloat())
    }
    open fun delete() {
        val ids = intArrayOf(id)
        GLES20.glDeleteTextures(1, ids, 0)
    }
    open fun bitmap(bitmap: Bitmap) {
        bind()
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        premultiplied = true
    }
    fun pixels(w: Int, h: Int, pixels: IntArray) {
        bind()
        val imageBuffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder()).asIntBuffer()
        imageBuffer.put(pixels)
        imageBuffer.position(0)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_RGBA,
            w,
            h,
            0,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            imageBuffer
        )
    }
    fun pixels(w: Int, h: Int, pixels: ByteArray) {
        bind()
        val imageBuffer = ByteBuffer.allocateDirect(w * h).order(ByteOrder.nativeOrder())
        imageBuffer.put(pixels)
        imageBuffer.position(0)
        GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1)
        GLES20.glTexImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            GLES20.GL_ALPHA,
            w,
            h,
            0,
            GLES20.GL_ALPHA,
            GLES20.GL_UNSIGNED_BYTE,
            imageBuffer
        )
    }
    // If getConfig returns null (unsupported format?), GLUtils.texImage2D works
    // incorrectly. In this case we need to load pixels manually
    fun handMade(bitmap: Bitmap, recode: Boolean) {
        val w = bitmap.width
        val h = bitmap.height
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        // recode - components reordering is needed
        if (recode) {
            for (i in pixels.indices) {
                val color = pixels[i]
                val ag = color and -0xff0100
                val r = (color shr 16) and 0xFF
                val b = color and 0xFF
                pixels[i] = ag or (b shl 16) or r
            }
        }
        pixels(w, h, pixels)
        premultiplied = false
    }
    companion object {
        const val NEAREST = GLES20.GL_NEAREST
        const val LINEAR = GLES20.GL_LINEAR
        const val REPEAT = GLES20.GL_REPEAT
        const val MIRROR = GLES20.GL_MIRRORED_REPEAT
        const val CLAMP = GLES20.GL_CLAMP_TO_EDGE
        fun activate(index: Int) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index)
        }
        fun create(bmp: Bitmap): Texture {
            val tex = Texture()
            tex.bitmap(bmp)
            return tex
        }
        fun create(width: Int, height: Int, pixels: IntArray): Texture {
            val tex = Texture()
            tex.pixels(width, height, pixels)
            return tex
        }
        fun create(width: Int, height: Int, pixels: ByteArray): Texture {
            val tex = Texture()
            tex.pixels(width, height, pixels)
            return tex
        }
    }
}
