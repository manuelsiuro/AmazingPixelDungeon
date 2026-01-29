package com.watabou.gltextures
import android.graphics.RectF
import java.util.HashMap
class Atlas(tx: SmartTexture) {
    var tx: SmartTexture = tx
    protected var namedFrames: HashMap<Any, RectF> = HashMap()
    protected var uvLeft: Float = 0f
    protected var uvTop: Float = 0f
    protected var uvWidth: Float = 0f
    protected var uvHeight: Float = 0f
    protected var cols: Int = 0
    init {
        tx.atlas = this
    }
    fun add(key: Any, left: Int, top: Int, right: Int, bottom: Int) {
        add(key, uvRect(tx, left, top, right, bottom))
    }
    fun add(key: Any, rect: RectF) {
        namedFrames[key] = rect
    }
    fun grid(width: Int) {
        grid(width, tx.height)
    }
    fun grid(width: Int, height: Int) {
        grid(0, 0, width, height, tx.width / width)
    }
    fun grid(left: Int, top: Int, width: Int, height: Int, cols: Int) {
        uvLeft = left.toFloat() / tx.width
        uvTop = top.toFloat() / tx.height
        uvWidth = width.toFloat() / tx.width
        uvHeight = height.toFloat() / tx.height
        this.cols = cols
    }
    operator fun get(index: Int): RectF {
        val x = (index % cols).toFloat()
        val y = (index / cols).toFloat()
        val l = uvLeft + x * uvWidth
        val t = uvTop + y * uvHeight
        return RectF(l, t, l + uvWidth, t + uvHeight)
    }
    operator fun get(key: Any): RectF? {
        return namedFrames[key]
    }
    fun width(rect: RectF): Float {
        return rect.width() * tx.width
    }
    fun height(rect: RectF): Float {
        return rect.height() * tx.height
    }
    companion object {
        fun uvRect(tx: SmartTexture, left: Int, top: Int, right: Int, bottom: Int): RectF {
            return RectF(
                left.toFloat() / tx.width,
                top.toFloat() / tx.height,
                right.toFloat() / tx.width,
                bottom.toFloat() / tx.height
            )
        }
    }
}
