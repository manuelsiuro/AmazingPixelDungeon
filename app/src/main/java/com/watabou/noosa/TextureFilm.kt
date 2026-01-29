package com.watabou.noosa
import android.graphics.RectF
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import java.util.HashMap
open class TextureFilm {
    private var texWidth: Int = 0
    private var texHeight: Int = 0
    protected var frames: HashMap<Any?, RectF> = HashMap()
    constructor(tx: Any) {
        val texture = TextureCache.get(tx)
        texWidth = texture.width
        texHeight = texture.height
        add(null, FULL)
    }
    constructor(texture: SmartTexture, width: Int) : this(texture, width, texture.height)
    constructor(tx: Any, width: Int, height: Int) {
        val texture = TextureCache.get(tx)
        texWidth = texture.width
        texHeight = texture.height
        val uw = width.toFloat() / texWidth
        val vh = height.toFloat() / texHeight
        val cols = texWidth / width
        val rows = texHeight / height
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rect = RectF(j * uw, i * vh, (j + 1) * uw, (i + 1) * vh)
                add(i * cols + j, rect)
            }
        }
    }
    constructor(atlas: TextureFilm, key: Any, width: Int, height: Int) {
        texWidth = atlas.texWidth
        texHeight = atlas.texHeight
        val patch = atlas[key]!!
        val uw = width.toFloat() / texWidth
        val vh = height.toFloat() / texHeight
        val cols = (width(patch) / width).toInt()
        val rows = (height(patch) / height).toInt()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val rect = RectF(j * uw, i * vh, (j + 1) * uw, (i + 1) * vh)
                rect.offset(patch.left, patch.top)
                add(i * cols + j, rect)
            }
        }
    }
    fun add(id: Any?, rect: RectF) {
        frames[id] = rect
    }
    operator fun get(id: Any?): RectF? {
        return frames[id]
    }
    fun width(frame: RectF): Float {
        return frame.width() * texWidth
    }
    fun height(frame: RectF): Float {
        return frame.height() * texHeight
    }
    companion object {
        private val FULL = RectF(0f, 0f, 1f, 1f)
    }
}
