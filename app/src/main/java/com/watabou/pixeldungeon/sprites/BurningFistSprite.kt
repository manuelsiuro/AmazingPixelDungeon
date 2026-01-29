package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.utils.Callback
class BurningFistSprite : MobSprite() {
    private var posToShoot: Int = 0
    init {
        texture(Assets.BURNING)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 24, 17)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 1)
        run = Animation(3, true)
        run?.frames(frames, 0, 1)
        attack = Animation(8, false)
        attack?.frames(frames, 0, 5, 6)
        die = Animation(10, false)
        die?.frames(frames, 0, 2, 3, 4)
        idle?.let { play(it) }
    }
    override fun attack(cell: Int) {
        posToShoot = cell
        super.attack(cell)
    }
    override fun onComplete(anim: Animation) {
        if (anim === attack) {
            Sample.play(Assets.SND_ZAP)
            val currentCh = ch ?: return
            parent?.let { p ->
                MagicMissile.shadow(p, currentCh.pos, posToShoot, object : Callback {
                    override fun call() { currentCh.onAttackComplete() }
                })
            }
            idle()
        } else {
            super.onComplete(anim)
        }
    }
}
