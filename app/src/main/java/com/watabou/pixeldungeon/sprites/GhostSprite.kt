package com.watabou.pixeldungeon.sprites
import android.opengl.GLES20
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ShaftParticle
import javax.microedition.khronos.opengles.GL10
class GhostSprite : MobSprite() {
    init {
        texture(Assets.GHOST)
        val frames = TextureFilm(checkNotNull(texture) { "Texture must be set" }, 14, 15)
        idle = Animation(5, true)
        idle?.frames(frames, 0, 1)
        run = Animation(10, true)
        run?.frames(frames, 0, 1)
        die = Animation(20, false)
        die?.frames(frames, 0)
        idle?.let { play(it) }
    }
    override fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
    override fun die() {
        super.die()
        emitter().start(ShaftParticle.FACTORY, 0.3f, 4)
        emitter().start(Speck.factory(Speck.LIGHT), 0.2f, 3)
    }
    override fun blood(): Int = 0xFFFFFF
}
