package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class KingSprite : MobSprite() {
    init {
        texture(Assets.KING)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(12, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2)
        run = Animation(15, true)
        run?.frames(frames, 3, 4, 5, 6, 7, 8)
        attack = Animation(15, false)
        attack?.frames(frames, 9, 10, 11)
        die = Animation(8, false)
        die?.frames(frames, 12, 13, 14, 15)
        idle?.let { play(it) }
    }
}
