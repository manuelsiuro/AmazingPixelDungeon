package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class GnollSprite : MobSprite() {
    init {
        texture(Assets.GNOLL)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(12, true)
        run?.frames(frames, 4, 5, 6, 7)
        attack = Animation(12, false)
        attack?.frames(frames, 2, 3, 0)
        die = Animation(12, false)
        die?.frames(frames, 8, 9, 10)
        idle?.let { play(it) }
    }
}
