package com.watabou.gltextures
import android.graphics.Bitmap
class Gradient(colors: IntArray) : SmartTexture(
    Bitmap.createBitmap(colors.size, 1, Bitmap.Config.ARGB_8888)
) {
    init {
        for (i in colors.indices) {
            bitmap!!.setPixel(i, 0, colors[i])
        }
        bitmap(bitmap!!) // Re-upload with pixel data
        filter(LINEAR, LINEAR)
        wrap(CLAMP, CLAMP)
        TextureCache.add(Gradient::class.java, this)
    }
}
