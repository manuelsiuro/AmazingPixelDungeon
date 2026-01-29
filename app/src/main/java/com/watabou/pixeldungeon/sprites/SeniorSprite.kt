package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.Random
class SeniorSprite : MobSprite() {
    private val kick: Animation
    init {
        texture(Assets.MONK)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 15, 14)
        idle = Animation(6, true)
        idle?.frames(frames, 18, 17, 18, 19)
        run = Animation(15, true)
        run?.frames(frames, 28, 29, 30, 31, 32, 33)
        attack = Animation(12, false)
        attack?.frames(frames, 20, 21, 20, 21)
        kick = Animation(10, false).also { it.frames(frames, 22, 23, 22) }
        die = Animation(15, false)
        die?.frames(frames, 18, 24, 25, 25, 26, 27)
        idle?.let { play(it) }
    }
    override fun attack(cell: Int) {
        super.attack(cell)
        if (Random.Float() < 0.3f) {
            play(kick)
        }
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(if (anim === kick) (attack ?: anim) else anim)
    }
}
