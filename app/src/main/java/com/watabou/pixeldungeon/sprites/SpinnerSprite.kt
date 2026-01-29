package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class SpinnerSprite : MobSprite() {
    init {
        texture(Assets.SPINNER)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 1, 0, 1)
        run = Animation(15, true)
        run?.frames(frames, 0, 2, 0, 3)
        attack = Animation(12, false)
        attack?.frames(frames, 0, 4, 5, 0)
        die = Animation(12, false)
        die?.frames(frames, 6, 7, 8, 9)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFFBFE5B8.toInt()
}
