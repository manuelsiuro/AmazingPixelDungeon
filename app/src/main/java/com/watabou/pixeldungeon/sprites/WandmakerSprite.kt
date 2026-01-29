package com.watabou.pixeldungeon.sprites
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Halo
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import javax.microedition.khronos.opengles.GL10
class WandmakerSprite : MobSprite() {
    private var shield: Shield? = null
    init {
        texture(Assets.MAKER)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 12, 14)
        idle = Animation(10, true)
        idle?.frames(frames, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3, 3, 3, 3, 3, 3, 2, 1)
        run = Animation(20, true)
        run?.frames(frames, 0)
        die = Animation(20, false)
        die?.frames(frames, 0)
        idle?.let { play(it) }
    }
    override fun link(ch: Char) {
        super.link(ch)
        if (shield == null) {
            val newShield = Shield()
            shield = newShield
            parent?.add(newShield)
        }
    }
    override fun die() {
        super.die()
        shield?.putOut()
        emitter().start(ElmoParticle.FACTORY, 0.03f, 60)
        if (visible) {
            Sample.play(Assets.SND_BURNING)
        }
    }
    inner class Shield : Halo(14f, 0xBBAACC, 1f) {
        private var phase: Float = 1f
        init {
            am = -1f
            aa = 1f
        }
        override fun update() {
            super.update()
            if (phase < 1) {
                phase -= Game.elapsed
                if (phase <= 0) {
                    killAndErase()
                } else {
                    scale.set((2 - phase) * radius / RADIUS)
                    am = phase * -1
                    aa = phase * 1
                }
            }
            visible = this@WandmakerSprite.visible
            if (visible) {
                val p = this@WandmakerSprite.center()
                point(p.x, p.y)
            }
        }
        override fun draw() {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            super.draw()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        }
        fun putOut() {
            phase = 0.999f
        }
    }
}
