package com.watabou.pixeldungeon.sprites

import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets

class ResourceCacheSprite : MobSprite() {
    init {
        texture(Assets.ITEMS)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(1, true)
        idle?.frames(frames, ItemSpriteSheet.RESOURCE_CACHE_ITEM)
        run = Animation(1, true)
        run?.frames(frames, ItemSpriteSheet.RESOURCE_CACHE_ITEM)
        die = Animation(1, false)
        die?.frames(frames, ItemSpriteSheet.RESOURCE_CACHE_ITEM)
        idle?.let { play(it) }
    }
}
