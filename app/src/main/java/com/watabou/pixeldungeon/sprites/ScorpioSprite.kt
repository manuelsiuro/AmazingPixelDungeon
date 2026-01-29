package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.items.weapon.missiles.Dart
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Callback
open class ScorpioSprite : MobSprite() {
    private var cellToAttack: Int = 0
    init {
        texture(Assets.SCORPIO)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 18, 17)
        idle = Animation(12, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 2)
        run = Animation(8, true)
        run?.frames(frames, 5, 5, 6, 6)
        attack = Animation(15, false)
        attack?.frames(frames, 0, 3, 4)
        zap = attack?.clone()
        die = Animation(12, false)
        die?.frames(frames, 0, 7, 8, 9, 10)
        idle?.let { play(it) }
    }
    override fun blood(): Int = 0xFF44FF22.toInt()
    override fun attack(cell: Int) {
        val currentCh = ch ?: return
        if (!Level.adjacent(cell, currentCh.pos)) {
            cellToAttack = cell
            turnTo(currentCh.pos, cell)
            zap?.let { play(it) }
        } else {
            super.attack(cell)
        }
    }
    override fun onComplete(anim: Animation) {
        if (anim === zap) {
            idle()
            val currentCh = ch ?: return
            (parent?.recycle(MissileSprite::class.java) as? MissileSprite)?.reset(
                currentCh.pos,
                cellToAttack,
                Dart(),
                object : Callback { override fun call() { currentCh.onAttackComplete() } }
            )
        } else {
            super.onComplete(anim)
        }
    }
}
