package com.watabou.pixeldungeon.sprites
import com.watabou.noosa.tweeners.AlphaTweener
import com.watabou.noosa.tweeners.ScaleTweener
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.utils.PointF
import com.watabou.utils.Random
open class MobSprite : CharSprite() {
    override fun update() {
        sleeping = (ch as? Mob)?.let { it.state == it.SLEEPING } ?: false
        super.update()
    }
    override fun onComplete(anim: Animation) {
        super.onComplete(anim)
        if (anim === die) {
            parent?.add(object : AlphaTweener(this@MobSprite, 0f, FADE_TIME) {
                override fun onComplete() {
                    this@MobSprite.killAndErase()
                    parent?.erase(this)
                }
            })
        }
    }
    fun fall() {
        origin.set(width / 2, height - DungeonTilemap.SIZE / 2)
        angularSpeed = if (Random.Int(2) == 0) -720f else 720f
        parent?.add(object : ScaleTweener(this@MobSprite, PointF(0f, 0f), FALL_TIME) {
            override fun onComplete() {
                this@MobSprite.killAndErase()
                parent?.erase(this)
            }
            override fun updateValues(progress: Float) {
                super.updateValues(progress)
                am = 1 - progress
            }
        })
    }
    companion object {
        private const val FADE_TIME = 3f
        private const val FALL_TIME = 1f
    }
}
