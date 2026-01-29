package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.Camera
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
class RottingFistSprite : MobSprite() {
    init {
        texture(Assets.ROTTING)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 24, 17)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 1)
        run = Animation(3, true)
        run?.frames(frames, 0, 1)
        attack = Animation(2, false)
        attack?.frames(frames, 0)
        die = Animation(10, false)
        die?.frames(frames, 0, 2, 3, 4)
        idle?.let { play(it) }
    }
    override fun attack(cell: Int) {
        super.attack(cell)
        speed.set(0f, -FALL_SPEED)
        acc.set(0f, FALL_SPEED * 4)
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === attack) {
            speed.set(0f)
            acc.set(0f)
            ch?.let { place(it.pos) }
            Camera.main?.shake(4f, 0.2f)
        }
    }
    companion object {
        private const val FALL_SPEED = 64f
    }
}
