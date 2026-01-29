package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.Splash
class YogSprite : MobSprite() {
    init {
        texture(Assets.YOG)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 20, 19)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 1, 2, 2, 1, 0, 3, 4, 4, 3, 0, 5, 6, 6, 5)
        run = Animation(12, true)
        run?.frames(frames, 0)
        attack = Animation(12, false)
        attack?.frames(frames, 0)
        die = Animation(10, false)
        die?.frames(frames, 0, 7, 8, 9)
        idle?.let { play(it) }
    }
    override fun die() {
        super.die()
        Splash.at(center(), blood(), 12)
    }
}
