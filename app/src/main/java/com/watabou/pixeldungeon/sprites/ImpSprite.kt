package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.effects.Speck
class ImpSprite : MobSprite() {
    init {
        texture(Assets.IMP)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 14)
        idle = Animation(10, true)
        idle?.frames(
            frames,
            0, 1, 2, 3, 0, 1, 2, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 3, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 4, 4, 4, 4, 4, 4, 4, 4
        )
        run = Animation(20, true)
        run?.frames(frames, 0)
        die = Animation(10, false)
        die?.frames(frames, 0, 3, 2, 1, 0, 3, 2, 1, 0)
        idle?.let { play(it) }
    }
    override fun link(ch: Char) {
        super.link(ch)
        if (ch is Imp) {
            alpha(0.4f)
        }
    }
    override fun onComplete(anim: Animation) {
        if (anim === die) {
            emitter().burst(Speck.factory(Speck.WOOL), 15)
            killAndErase()
        } else {
            super.onComplete(anim)
        }
    }
}
