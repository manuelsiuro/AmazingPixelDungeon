package com.watabou.gltextures

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader

object TextureCache {

    var context: Context? = null

    private val all = HashMap<Any, SmartTexture>()

    private val bitmapOptions = BitmapFactory.Options().apply {
        inScaled = false
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    fun createSolid(color: Int): SmartTexture {
        val key = "1x1:$color"
        return all.getOrPut(key) {
            val bmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            bmp.eraseColor(color)
            SmartTexture(bmp)
        }
    }

    fun createGradient(width: Int, height: Int, vararg colors: Int): SmartTexture {
        val key = "${width}x${height}:${colors.contentToString()}"
        return all.getOrPut(key) {
            val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            val paint = Paint().apply {
                shader = LinearGradient(0f, 0f, 0f, height.toFloat(), colors, null, Shader.TileMode.CLAMP)
            }
            canvas.drawPaint(paint)
            SmartTexture(bmp)
        }
    }

    fun add(key: Any, tx: SmartTexture) {
        all[key] = tx
    }

    fun get(src: Any): SmartTexture {
        return all[src]
            ?: if (src is SmartTexture) src
            else {
                val tx = SmartTexture(requireNotNull(getBitmap(src)) { "Failed to load bitmap for: $src" })
                all[src] = tx
                tx
            }
    }

    fun clear() {
        for (txt in all.values) {
            txt.delete()
        }
        all.clear()
    }

    fun reload() {
        for (tx in all.values) {
            tx.reload()
        }
    }

    fun getBitmap(src: Any): Bitmap? {
        return try {
            val ctx = requireNotNull(context) { "TextureCache.context must be set before loading bitmaps" }
            when (src) {
                is Int -> BitmapFactory.decodeResource(ctx.resources, src, bitmapOptions)
                is String -> BitmapFactory.decodeStream(ctx.assets.open(src), null, bitmapOptions)
                is Bitmap -> src
                else -> null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun contains(key: Any): Boolean {
        return all.containsKey(key)
    }
}
