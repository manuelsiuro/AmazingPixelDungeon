package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Assets
class Effects {
    enum class Type {
        RIPPLE,
        LIGHTNING,
        WOUND,
        RAY
    }
    companion object {
        fun get(type: Type): Image {
            val icon = Image(Assets.EFFECTS)
            val texture = requireNotNull(icon.texture) { "Effects texture must not be null" }
            when (type) {
                Type.RIPPLE -> icon.frame(texture.uvRect(0, 0, 16, 16))
                Type.LIGHTNING -> icon.frame(texture.uvRect(16, 0, 32, 8))
                Type.WOUND -> icon.frame(texture.uvRect(16, 8, 32, 16))
                Type.RAY -> icon.frame(texture.uvRect(16, 16, 32, 24))
            }
            return icon
        }
    }
}
