package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.scenes.GameScene
class PiranhaSprite : MobSprite() {
    init {
        texture(Assets.PIRANHA)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 16)
        idle = Animation(8, true)
        idle?.frames(frames, 0, 1, 2, 1)
        run = Animation(20, true)
        run?.frames(frames, 0, 1, 2, 1)
        attack = Animation(20, false)
        attack?.frames(frames, 3, 4, 5, 6, 7, 8, 9, 10, 11)
        die = Animation(4, false)
        die?.frames(frames, 12, 13, 14)
        idle?.let { play(it) }
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === attack) {
            ch?.let { GameScene.ripple(it.pos) }
        }
    }
}
