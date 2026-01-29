package com.watabou.noosa.particles
import com.watabou.noosa.Game
import com.watabou.noosa.PseudoPixel
open class PixelParticle : PseudoPixel {
    protected var size: Float = 0f
    protected var lifespan: Float = 0f
    protected var left: Float = 0f
    constructor() : super() {
        origin.set(+0.5f)
    }
    fun reset(x: Float, y: Float, color: Int, size: Float, lifespan: Float) {
        revive()
        this.x = x
        this.y = y
        color(color)
        size(size.also { this.size = it })
        this.lifespan = lifespan
        this.left = lifespan
    }
    override fun update() {
        super.update()
        left -= Game.elapsed
        if (left <= 0) {
            kill()
        }
    }
    open class Shrinking : PixelParticle() {
        override fun update() {
            super.update()
            size(size * left / lifespan)
        }
    }
}
