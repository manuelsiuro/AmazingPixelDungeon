package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class AcidicSprite : ScorpioSprite() {
    init {
        texture(Assets.SCORPIO)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 18, 17)
        idle = Animation(12, true)
        idle?.frames(frames, 14, 14, 14, 14, 14, 14, 14, 14, 15, 16, 15, 16, 15, 16)
        run = Animation(4, true)
        run?.frames(frames, 19, 20)
        attack = Animation(15, false)
        attack?.frames(frames, 14, 17, 18)
        zap = attack?.clone()
        die = Animation(12, false)
        die?.frames(frames, 14, 21, 22, 23, 24)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFF66FF22.toInt()
}
