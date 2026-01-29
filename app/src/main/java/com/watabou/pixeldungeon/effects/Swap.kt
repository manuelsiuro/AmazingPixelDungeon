package com.watabou.pixeldungeon.effects
import com.watabou.noosa.Game
import com.watabou.noosa.Visual
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.utils.PointF
class Swap(private val ch1: Char, private val ch2: Char) : Actor() {
    private var eff1: Effect?
    private var eff2: Effect?
    private val delay: Float
    init {
        delay = Level.distance(ch1.pos, ch2.pos) * 0.1f
        val sprite1 = requireNotNull(ch1.sprite) { "ch1.sprite must not be null for Swap" }
        val sprite2 = requireNotNull(ch2.sprite) { "ch2.sprite must not be null for Swap" }
        eff1 = Effect(sprite1, ch1.pos, ch2.pos)
        eff2 = Effect(sprite2, ch2.pos, ch1.pos)
        Sample.play(Assets.SND_TELEPORT)
    }
    override fun act(): Boolean {
        return false
    }
    private fun finish(eff: Effect) {
        if (eff == eff1) {
            eff1 = null
        }
        if (eff == eff2) {
            eff2 = null
        }
        if (eff1 == null && eff2 == null) {
            Actor.remove(this)
            next()
            val pos = ch1.pos
            ch1.pos = ch2.pos
            ch2.pos = pos
            if (!ch1.flying) {
                if (ch1 is Mob) {
                    Dungeon.level!!.mobPress(ch1)
                } else {
                    Dungeon.level!!.press(ch1.pos, ch1)
                }
            }
            if (!ch2.flying) {
                if (ch2 is Mob) {
                    Dungeon.level!!.mobPress(ch2)
                } else {
                    Dungeon.level!!.press(ch2.pos, ch2)
                }
            }
            if (ch1 == Dungeon.hero || ch2 == Dungeon.hero) {
                Dungeon.observe()
            }
        }
    }
    private inner class Effect(private val sprite: CharSprite, from: Int, to: Int) : Visual(0f, 0f, 0f, 0f) {
        private val end: PointF
        private var passed: Float
        init {
            point(sprite.worldToCamera(from))
            end = sprite.worldToCamera(to)
            speed.set(2 * (end.x - x) / delay, 2 * (end.y - y) / delay)
            acc.set(-speed.x / delay, -speed.y / delay)
            passed = 0f
            sprite.parent?.add(this)
        }
        override fun update() {
            super.update()
            passed += Game.elapsed
            if (passed < delay) {
                sprite.x = x
                sprite.y = y
            } else {
                sprite.point(end)
                killAndErase()
                finish(this)
            }
        }
    }
}
