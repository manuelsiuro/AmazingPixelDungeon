package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.Random
class MonkSprite : MobSprite() {
    private val kick: Animation
    init {
        texture(Assets.MONK)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 15, 14)
        idle = Animation(6, true)
        idle?.frames(frames, 1, 0, 1, 2)
        run = Animation(15, true)
        run?.frames(frames, 11, 12, 13, 14, 15, 16)
        attack = Animation(12, false)
        attack?.frames(frames, 3, 4, 3, 4)
        kick = Animation(10, false).also { it.frames(frames, 5, 6, 5) }
        die = Animation(15, false)
        die?.frames(frames, 1, 7, 8, 8, 9, 10)
        idle?.let { play(it) }
    }
    override fun attack(cell: Int) {
        super.attack(cell)
        if (Random.Float() < 0.5f) {
            play(kick)
        }
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(if (anim === kick) (attack ?: anim) else anim)
    }
}
