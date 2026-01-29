package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.items.weapon.missiles.Shuriken
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Callback
class TenguSprite : MobSprite() {
    private var cast: Animation? = null
    init {
        texture(Assets.TENGU)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 14, 16)
        idle = Animation(2, true)
        idle?.frames(frames, 0, 0, 0, 1)
        run = Animation(15, false)
        run?.frames(frames, 2, 3, 4, 5, 0)
        attack = Animation(15, false)
        attack?.frames(frames, 6, 7, 7, 0)
        cast = attack?.clone()
        die = Animation(8, false)
        die?.frames(frames, 8, 9, 10, 10, 10, 10, 10, 10)
        run?.clone()?.let { play(it) }
    }
    override fun move(from: Int, to: Int) {
        place(to)
        run?.let { play(it) }
        turnTo(from, to)
        isMoving = true
        if (Level.water[to]) {
            GameScene.ripple(to)
        }
        ch?.onMotionComplete()
    }
    override fun attack(cell: Int) {
        val currentCh = ch ?: return
        if (!Level.adjacent(cell, currentCh.pos)) {
            (parent?.recycle(MissileSprite::class.java) as? MissileSprite)?.reset(
                currentCh.pos,
                cell,
                Shuriken(),
                object : Callback { override fun call() { currentCh.onAttackComplete() } }
            )
            cast?.let { play(it) }
            turnTo(currentCh.pos, cell)
        } else {
            super.attack(cell)
        }
    }
    override fun onComplete(anim: Animation) {
        if (anim === run) {
            isMoving = false
            idle()
        } else {
            super.onComplete(anim)
        }
    }
}
