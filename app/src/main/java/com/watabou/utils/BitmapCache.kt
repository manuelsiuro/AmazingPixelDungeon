package com.watabou.utils
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.util.HashMap
object BitmapCache {
    private const val DEFAULT = "__default"
    private val layers = HashMap<String, Layer>()
    private val opts = BitmapFactory.Options()
    var context: Context? = null
    fun get(assetName: String): Bitmap? {
        return get(DEFAULT, assetName)
    }
    fun get(layerName: String, assetName: String): Bitmap? {
        val layer: Layer
        if (!layers.containsKey(layerName)) {
            layer = Layer()
            layers[layerName] = layer
        } else {
            layer = layers[layerName]!!
        }
        if (layer.containsKey(assetName)) {
            return layer[assetName]
        } else {
            try {
                val stream = context!!.resources.assets.open(assetName)
                val bmp = BitmapFactory.decodeStream(stream, null, opts)
                if (bmp != null) {
                    layer[assetName] = bmp
                }
                return bmp
            } catch (e: IOException) {
                return null
            }
        }
    }
    fun get(resID: Int): Bitmap? {
        return get(DEFAULT, resID)
    }
    fun get(layerName: String, resID: Int): Bitmap? {
        val layer: Layer
        if (!layers.containsKey(layerName)) {
            layer = Layer()
            layers[layerName] = layer
        } else {
            layer = layers[layerName]!!
        }
        if (layer.containsKey(resID)) {
            return layer[resID]
        } else {
            val bmp = BitmapFactory.decodeResource(context!!.resources, resID)
            if (bmp != null) {
                layer[resID] = bmp
            }
            return bmp
        }
    }
    fun clear(layerName: String) {
        if (layers.containsKey(layerName)) {
            layers[layerName]!!.clear()
            layers.remove(layerName)
        }
    }
    fun clear() {
        for (layer in layers.values) {
            layer.clear()
        }
        layers.clear()
    }
    private class Layer : HashMap<Any, Bitmap>() {
        override fun clear() {
            for (bmp in values) {
                bmp.recycle()
            }
            super.clear()
        }
    }
}
