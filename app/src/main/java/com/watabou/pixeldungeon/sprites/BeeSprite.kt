package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class BeeSprite : MobSprite() {
    init {
        texture(Assets.BEE)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(12, true)
        idle?.frames(frames, 0, 1, 1, 0, 2, 2)
        run = Animation(15, true)
        run?.frames(frames, 0, 1, 1, 0, 2, 2)
        attack = Animation(20, false)
        attack?.frames(frames, 3, 4, 5, 6)
        die = Animation(20, false)
        die?.frames(frames, 7, 8, 9, 10)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xffd500
}
