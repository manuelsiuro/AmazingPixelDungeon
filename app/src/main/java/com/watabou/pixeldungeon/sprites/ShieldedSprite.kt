package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class ShieldedSprite : MobSprite() {
    init {
        texture(Assets.BRUTE)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 16)
        idle = Animation(2, true)
        idle?.frames(frames, 21, 21, 21, 22, 21, 21, 22, 22)
        run = Animation(12, true)
        run?.frames(frames, 25, 26, 27, 28)
        attack = Animation(12, false)
        attack?.frames(frames, 23, 24)
        die = Animation(12, false)
        die?.frames(frames, 29, 30, 31)
        idle?.let { play(it) }
    }
}
