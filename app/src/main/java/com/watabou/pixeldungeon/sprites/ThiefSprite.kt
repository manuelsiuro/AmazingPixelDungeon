package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class ThiefSprite : MobSprite() {
    init {
        texture(Assets.THIEF)
        val film = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 13)
        idle = Animation(1, true)
        idle?.frames(film, 0, 0, 0, 1, 0, 0, 0, 0, 1)
        run = Animation(15, true)
        run?.frames(film, 0, 0, 2, 3, 3, 4)
        die = Animation(10, false)
        die?.frames(film, 5, 6, 7, 8, 9)
        attack = Animation(12, false)
        attack?.frames(film, 10, 11, 12, 0)
        idle()
    }
}
