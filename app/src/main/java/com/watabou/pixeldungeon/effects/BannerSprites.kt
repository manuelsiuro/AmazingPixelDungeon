package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Assets
class BannerSprites {
    enum class Type {
        PIXEL_DUNGEON,
        BOSS_SLAIN,
        GAME_OVER,
        SELECT_YOUR_HERO,
        PIXEL_DUNGEON_SIGNS
    }
    companion object {
        fun get(type: Type): Image {
            val icon = Image(Assets.BANNERS)
            val texture = requireNotNull(icon.texture) { "BannerSprites texture must not be null" }
            when (type) {
                Type.PIXEL_DUNGEON -> icon.frame(texture.uvRect(0, 0, 128, 70))
                Type.BOSS_SLAIN -> icon.frame(texture.uvRect(0, 70, 128, 105))
                Type.GAME_OVER -> icon.frame(texture.uvRect(0, 105, 128, 140))
                Type.SELECT_YOUR_HERO -> icon.frame(texture.uvRect(0, 140, 128, 161))
                Type.PIXEL_DUNGEON_SIGNS -> icon.frame(texture.uvRect(0, 161, 128, 218))
            }
            return icon
        }
    }
}
