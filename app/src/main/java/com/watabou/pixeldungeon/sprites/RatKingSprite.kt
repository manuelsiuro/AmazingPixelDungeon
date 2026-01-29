package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class RatKingSprite : MobSprite() {
    init {
        texture(Assets.RATKING)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 2, 3, 4, 5, 6)
        attack = Animation(15, false)
        attack?.frames(frames, 0)
        die = Animation(10, false)
        die?.frames(frames, 0)
        idle?.let { play(it) }
    }
}
