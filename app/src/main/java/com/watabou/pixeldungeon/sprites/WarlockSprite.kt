package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.mobs.Warlock
import com.watabou.pixeldungeon.effects.MagicMissile
import com.watabou.utils.Callback
class WarlockSprite : MobSprite() {
    init {
        texture(Assets.WARLOCK)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 15)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(15, true)
        run?.frames(frames, 0, 2, 3, 4)
        attack = Animation(12, false)
        attack?.frames(frames, 0, 5, 6)
        zap = attack?.clone()
        die = Animation(15, false)
        die?.frames(frames, 0, 7, 8, 8, 9, 10)
        idle?.let { play(it) }
    }
    override fun zap(cell: Int) {
        val currentCh = ch ?: return
        turnTo(currentCh.pos, cell)
        zap?.let { play(it) }
        parent?.let { p ->
            MagicMissile.shadow(p, currentCh.pos, cell, object : Callback {
                override fun call() {
                    (currentCh as? Warlock)?.onZapComplete()
                }
            })
        }
        Sample.play(Assets.SND_ZAP)
    }
    override fun onComplete(anim: Animation) {
        if (anim === zap) {
            idle()
        }
        super.onComplete(anim)
    }
}
