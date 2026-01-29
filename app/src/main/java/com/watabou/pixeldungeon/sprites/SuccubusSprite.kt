package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
class SuccubusSprite : MobSprite() {
    init {
        texture(Assets.SUCCUBUS)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 15)
        idle = Animation(8, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 1)
        run = Animation(15, true)
        run?.frames(frames, 3, 4, 5, 6, 7, 8)
        attack = Animation(12, false)
        attack?.frames(frames, 9, 10, 11)
        die = Animation(12, false)
        die?.frames(frames, 12)
        idle?.let { play(it) }
    }
    override fun die() {
        super.die()
        emitter().burst(Speck.factory(Speck.HEART), 6)
        emitter().burst(ShadowParticle.UP, 8)
    }
}
