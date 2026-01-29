package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class StatueSprite : MobSprite() {
    init {
        texture(Assets.STATUE)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 1, 1)
        run = Animation(15, true)
        run?.frames(frames, 2, 3, 4, 5, 6, 7)
        attack = Animation(12, false)
        attack?.frames(frames, 8, 9, 10)
        die = Animation(5, false)
        die?.frames(frames, 11, 12, 13, 14, 15, 15)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFFcdcdb7.toInt()
}
