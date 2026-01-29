package com.watabou.pixeldungeon.effects
import com.watabou.gltextures.SmartTexture
import com.watabou.noosa.NinePatch
import com.watabou.pixeldungeon.Assets
class ShadowBox : NinePatch(Assets.SHADOW, 1) {
    init {
        texture?.filter(com.watabou.glwrap.Texture.LINEAR, com.watabou.glwrap.Texture.LINEAR)
        scale.set(SIZE, SIZE)
    }
    override fun size(width: Float, height: Float) {
        super.size(width / SIZE, height / SIZE)
    }
    fun boxRect(x: Float, y: Float, width: Float, height: Float) {
        this.x = x - SIZE
        this.y = y - SIZE
        size(width + SIZE * 2, height + SIZE * 2)
    }
    companion object {
        const val SIZE = 16f
    }
}
