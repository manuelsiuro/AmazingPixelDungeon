package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class CrabSprite : MobSprite() {
    init {
        texture(Assets.CRAB)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16)
        idle = Animation(5, true)
        idle?.frames(frames, 0, 1, 0, 2)
        run = Animation(15, true)
        run?.frames(frames, 3, 4, 5, 6)
        attack = Animation(12, false)
        attack?.frames(frames, 7, 8, 9)
        die = Animation(12, false)
        die?.frames(frames, 10, 11, 12, 13)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFFFFEA80.toInt()
}
