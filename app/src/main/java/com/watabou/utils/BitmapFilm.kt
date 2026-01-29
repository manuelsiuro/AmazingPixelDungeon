package com.watabou.utils
import android.graphics.Bitmap
import android.graphics.Rect
import java.util.HashMap
class BitmapFilm(
    var bitmap: Bitmap,
    width: Int = 0,
    height: Int = 0
) {
    protected var frames = HashMap<Any?, Rect>()
    init {
        if (width == 0 && height == 0) {
            add(null, Rect(0, 0, bitmap.width, bitmap.height))
        } else {
            val effectiveHeight = if (height == 0) bitmap.height else height
            val cols = bitmap.width / width
            val rows = bitmap.height / effectiveHeight
            for (i in 0 until rows) {
                for (j in 0 until cols) {
                    val rect = Rect(j * width, i * effectiveHeight, (j + 1) * width, (i + 1) * effectiveHeight)
                    add(i * cols + j, rect)
                }
            }
        }
    }
    constructor(bitmap: Bitmap) : this(bitmap, 0, 0)
    fun add(id: Any?, rect: Rect) {
        frames[id] = rect
    }
    fun get(id: Any?): Rect? {
        return frames[id]
    }
}
