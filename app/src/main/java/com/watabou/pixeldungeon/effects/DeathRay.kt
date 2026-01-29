package com.watabou.pixeldungeon.effects
import android.opengl.GLES20
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.utils.PointF
import javax.microedition.khronos.opengles.GL10
class DeathRay(s: PointF, e: PointF) : Image(Effects.get(Effects.Type.RAY)) {
    private var timeLeft: Float
    init {
        origin.set(0f, height / 2f)
        x = s.x - origin.x
        y = s.y - origin.y
        val dx = e.x - s.x
        val dy = e.y - s.y
        angle = (Math.atan2(dy.toDouble(), dx.toDouble()) * A).toFloat()
        scale.x = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat() / width
        Sample.play(Assets.SND_RAY)
        timeLeft = DURATION
    }
    override fun update() {
        super.update()
        val p = timeLeft / DURATION
        alpha(p)
        scale.set(scale.x, p)
        timeLeft -= Game.elapsed
        if (timeLeft <= 0) {
            killAndErase()
        }
    }
    override fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
    companion object {
        private const val A = 180 / Math.PI
        private const val DURATION = 0.5f
    }
}
