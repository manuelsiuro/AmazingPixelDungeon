package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets

class StorageChestSprite : MobSprite() {
    init {
        texture(Assets.ITEMS)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(1, true)
        idle?.frames(frames, ItemSpriteSheet.STORAGE_CHEST)
        run = Animation(1, true)
        run?.frames(frames, ItemSpriteSheet.STORAGE_CHEST)
        die = Animation(1, false)
        die?.frames(frames, ItemSpriteSheet.STORAGE_CHEST)
        idle?.let { play(it) }
    }
}
