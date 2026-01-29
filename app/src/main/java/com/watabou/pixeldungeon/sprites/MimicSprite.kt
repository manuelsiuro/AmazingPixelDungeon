package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class MimicSprite : MobSprite() {
    init {
        texture(Assets.MIMIC)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(5, true)
        idle?.frames(frames, 0, 0, 0, 1, 1)
        run = Animation(10, true)
        run?.frames(frames, 0, 1, 2, 3, 3, 2, 1)
        attack = Animation(10, false)
        attack?.frames(frames, 0, 4, 5, 6)
        die = Animation(5, false)
        die?.frames(frames, 7, 8, 9)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFFcb9700.toInt()
}
