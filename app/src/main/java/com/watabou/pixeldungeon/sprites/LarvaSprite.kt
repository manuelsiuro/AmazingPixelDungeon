package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.Splash
class LarvaSprite : MobSprite() {
    init {
        texture(Assets.LARVA)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 8)
        idle = Animation(5, true)
        idle?.frames(frames, 4, 4, 4, 4, 4, 5, 5)
        run = Animation(12, true)
        run?.frames(frames, 0, 1, 2, 3)
        attack = Animation(15, false)
        attack?.frames(frames, 6, 5, 7)
        die = Animation(10, false)
        die?.frames(frames, 8)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xbbcc66
    override fun die() {
        Splash.at(center(), blood(), 10)
        super.die()
    }
}
