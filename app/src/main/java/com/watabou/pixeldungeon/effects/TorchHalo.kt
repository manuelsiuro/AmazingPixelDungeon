package com.watabou.pixeldungeon.effects
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.sprites.CharSprite
import javax.microedition.khronos.opengles.GL10
class TorchHalo(private val target: CharSprite) : Halo(24.0f, 0xFFDDCC, 0.15f) {
    private var phase = 0f
    init {
        am = 0f
    }
    override fun update() {
        super.update()
        if (phase < 0) {
            phase += Game.elapsed
            if (phase >= 0) {
                killAndErase()
            } else {
                scale.set((2 + phase) * radius / Halo.RADIUS)
                am = -phase * brightness
            }
        } else if (phase < 1) {
            phase += Game.elapsed
            if (phase >= 1) {
                phase = 1f
            }
            scale.set(phase * radius / Halo.RADIUS)
            am = phase * brightness
        }
        point(target.x + target.width / 2, target.y + target.height / 2)
    }
    override fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
    fun putOut() {
        phase = -1f
    }
}
