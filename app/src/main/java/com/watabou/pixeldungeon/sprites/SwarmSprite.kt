package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class SwarmSprite : MobSprite() {
    init {
        texture(Assets.SWARM)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(15, true)
        idle?.frames(frames, 0, 1, 2, 3, 4, 5)
        run = Animation(15, true)
        run?.frames(frames, 0, 1, 2, 3, 4, 5)
        attack = Animation(20, false)
        attack?.frames(frames, 6, 7, 8, 9)
        die = Animation(15, false)
        die?.frames(frames, 10, 11, 12, 13, 14)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFF8BA077.toInt()
}
