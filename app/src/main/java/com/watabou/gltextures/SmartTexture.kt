package com.watabou.gltextures
import android.graphics.Bitmap
import android.graphics.RectF
import com.watabou.glwrap.Texture
open class SmartTexture(bitmap: Bitmap, filtering: Int, wrapping: Int) : Texture() {
    var width: Int = 0
    var height: Int = 0
    var fModeMin: Int = 0
    var fModeMax: Int = 0
    var wModeH: Int = 0
    var wModeV: Int = 0
    var bitmap: Bitmap? = null
    var atlas: Atlas? = null
    constructor(bitmap: Bitmap) : this(bitmap, NEAREST, CLAMP)
    init {
        bitmap(bitmap)
        filter(filtering, filtering)
        wrap(wrapping, wrapping)
    }
    override fun filter(minMode: Int, maxMode: Int) {
        fModeMin = minMode
        fModeMax = maxMode
        super.filter(minMode, maxMode)
    }
    override fun wrap(s: Int, t: Int) {
        wModeH = s
        wModeV = t
        super.wrap(s, t)
    }
    override fun bitmap(bitmap: Bitmap) {
        bitmap(bitmap, false)
    }
    fun bitmap(bitmap: Bitmap, premultiplied: Boolean) {
        if (premultiplied) {
            super.bitmap(bitmap)
        } else {
            handMade(bitmap, true)
        }
        this.bitmap = bitmap
        width = bitmap.width
        height = bitmap.height
    }
    open fun reload() {
        if (bitmap != null) {
            id = SmartTexture(bitmap!!).id
            filter(fModeMin, fModeMax)
            wrap(wModeH, wModeV)
        }
    }
    override fun delete() {
        super.delete()
        bitmap?.recycle()
        bitmap = null
    }
    fun uvRect(left: Int, top: Int, right: Int, bottom: Int): RectF {
        return RectF(
            left.toFloat() / width,
            top.toFloat() / height,
            right.toFloat() / width,
            bottom.toFloat() / height
        )
    }
}
