package com.watabou.noosa
import com.watabou.gltextures.TextureCache
open class ColorBlock(width: Float, height: Float, color: Int) : Image(TextureCache.createSolid(color)), Resizable {
    init {
        scale.set(width, height)
        origin.set(0f, 0f)
    }
    override fun size(width: Float, height: Float) {
        scale.set(width, height)
    }
    override fun width(): Float {
        return scale.x
    }
    override fun height(): Float {
        return scale.y
    }
}
