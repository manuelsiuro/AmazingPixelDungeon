package com.watabou.noosa
import com.watabou.gltextures.TextureCache
open class PseudoPixel : Image {
    constructor() : super(TextureCache.createSolid(-0x1))
    constructor(x: Float, y: Float, color: Int) : this() {
        this.x = x
        this.y = y
        color(color)
    }
    fun size(w: Float, h: Float) {
        scale.set(w, h)
    }
    fun size(value: Float) {
        scale.set(value)
    }
}
