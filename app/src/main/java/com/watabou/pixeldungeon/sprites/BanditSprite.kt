package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class BanditSprite : MobSprite() {
    init {
        texture(Assets.THIEF)
        val film = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 13)
        idle = Animation(1, true)
        idle?.frames(film, 21, 21, 21, 22, 21, 21, 21, 21, 22)
        run = Animation(15, true)
        run?.frames(film, 21, 21, 23, 24, 24, 25)
        die = Animation(10, false)
        die?.frames(film, 25, 27, 28, 29, 30)
        attack = Animation(12, false)
        attack?.frames(film, 31, 32, 33)
        idle()
    }
}
