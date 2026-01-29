package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.Speck
class DM300Sprite : MobSprite() {
    init {
        texture(Assets.DM300)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 22, 20)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 2, 3)
        attack = Animation(15, false)
        attack?.frames(frames, 4, 5, 6)
        die = Animation(20, false)
        die?.frames(frames, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 0, 7, 8)
        idle?.let { play(it) }
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === die) {
            emitter().burst(Speck.factory(Speck.WOOL), 15)
        }
    }
    override fun blood(): Int = 0xFFFFFF88.toInt()
}
