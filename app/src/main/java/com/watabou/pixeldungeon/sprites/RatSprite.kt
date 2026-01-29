package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
open class RatSprite : MobSprite() {
    init {
        texture(Assets.RAT)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 6, 7, 8, 9, 10)
        attack = Animation(15, false)
        attack?.frames(frames, 2, 3, 4, 5, 0)
        die = Animation(10, false)
        die?.frames(frames, 11, 12, 13, 14)
        idle?.let { play(it) }
    }
}
