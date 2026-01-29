package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
class GolemSprite : MobSprite() {
    init {
        texture(Assets.GOLEM)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 16, 16)
        idle = Animation(4, true)
        idle?.frames(frames, 0, 1)
        run = Animation(12, true)
        run?.frames(frames, 2, 3, 4, 5)
        attack = Animation(10, false)
        attack?.frames(frames, 6, 7, 8)
        die = Animation(15, false)
        die?.frames(frames, 9, 10, 11, 12, 13)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFF80706c.toInt()
    override fun onComplete(anim: Animation) {
        if (anim === die) {
            emitter().burst(ElmoParticle.FACTORY, 4)
        }
        super.onComplete(anim)
    }
}
