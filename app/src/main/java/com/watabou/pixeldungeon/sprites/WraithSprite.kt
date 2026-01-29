package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
open class WraithSprite : MobSprite() {
    init {
        texture(Assets.WRAITH)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 14, 15)
        idle = Animation(5, true)
        idle?.frames(frames, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 0, 1)
        attack = Animation(10, false)
        attack?.frames(frames, 0, 2, 3)
        die = Animation(8, false)
        die?.frames(frames, 0, 4, 5, 6, 7)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0x88000000.toInt()
}
