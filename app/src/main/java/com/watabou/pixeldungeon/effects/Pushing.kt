package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Visual
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.utils.PointF
class Pushing(ch: Char, private val from: Int, private val to: Int) : Actor() {
    private val sprite: CharSprite? = ch.sprite
    private var effect: Effect? = null
    override fun act(): Boolean {
        if (sprite == null) {
            Actor.remove(this@Pushing)
            return true
        }
        return if (effect == null) {
            Effect()
            false
        } else {
            Actor.remove(this@Pushing)
            true
        }
    }
    inner class Effect : Visual(0f, 0f, 0f, 0f) {
        private val end: PointF
        private var delay: Float
        init {
            val s = requireNotNull(sprite) { "Sprite must not be null in Pushing.Effect" }
            point(s.worldToCamera(from))
            end = s.worldToCamera(to)
            speed.set(2 * (end.x - x) / DELAY, 2 * (end.y - y) / DELAY)
            acc.set(-speed.x / DELAY, -speed.y / DELAY)
            delay = 0f
            s.parent?.add(this)
        }
        override fun update() {
            super.update()
            val s = sprite ?: return
            delay += Game.elapsed
            if (delay < DELAY) {
                s.x = x
                s.y = y
            } else {
                s.point(end)
                killAndErase()
                Actor.remove(this@Pushing)
                next()
            }
        }
    }
    companion object {
        private const val DELAY = 0.15f
    }
}
