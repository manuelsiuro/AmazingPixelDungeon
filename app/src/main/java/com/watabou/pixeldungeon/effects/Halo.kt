package com.watabou.pixeldungeon.effects
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Image
open class Halo : Image {
    protected var radius: Float = RADIUS.toFloat()
    protected var brightness: Float = 1f
    constructor() : super() {
        if (!TextureCache.contains(CACHE_KEY)) {
            val bmp = Bitmap.createBitmap(RADIUS * 2, RADIUS * 2, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint()
            paint.color = 0xFFFFFFFF.toInt()
            canvas.drawCircle(RADIUS.toFloat(), RADIUS.toFloat(), RADIUS * 0.75f, paint)
            paint.color = 0x88FFFFFF.toInt()
            canvas.drawCircle(RADIUS.toFloat(), RADIUS.toFloat(), RADIUS.toFloat(), paint)
            TextureCache.add(CACHE_KEY, SmartTexture(bmp))
        }
        texture(CACHE_KEY)
        origin.set(RADIUS.toFloat())
    }
    constructor(radius: Float, color: Int, brightness: Float) : this() {
        hardlight(color)
        alpha(brightness)
        this.brightness = brightness
        radius(radius)
    }
    fun point(x: Float, y: Float): Halo {
        this.x = x - RADIUS
        this.y = y - RADIUS
        return this
    }
    fun radius(value: Float) {
        radius = value
        scale.set(radius / RADIUS)
    }
    companion object {
        private val CACHE_KEY: Any = Halo::class.java
        const val RADIUS = 64
    }
}
