package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class AlbinoSprite : MobSprite() {
    init {
        texture(Assets.RAT)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 16, 16, 16, 17)
        run = Animation(10, true)
        run?.frames(frames, 22, 23, 24, 25, 26)
        attack = Animation(15, false)
        attack?.frames(frames, 18, 19, 20, 21)
        die = Animation(10, false)
        die?.frames(frames, 27, 28, 29, 30)
        idle?.let { play(it) }
    }
}
