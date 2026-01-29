package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class BatSprite : MobSprite() {
    init {
        texture(Assets.BAT)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 15, 15)
        idle = Animation(8, true)
        idle?.frames(frames, 0, 1)
        run = Animation(12, true)
        run?.frames(frames, 0, 1)
        attack = Animation(12, false)
        attack?.frames(frames, 2, 3, 0, 1)
        die = Animation(12, false)
        die?.frames(frames, 4, 5, 6)
        idle?.let { play(it) }
    }
}
