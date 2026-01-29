package com.watabou.pixeldungeon.effects
import android.opengl.GLES20
import com.watabou.noosa.Group
import com.watabou.noosa.particles.PixelParticle
import com.watabou.utils.PointF
import com.watabou.utils.Random
import javax.microedition.khronos.opengles.GL10
class Degradation private constructor(p: PointF, matrix: IntArray) : Group() {
    init {
        var i = 0
        while (i < matrix.size) {
            add(Speck(p.x, p.y, matrix[i], matrix[i + 1]))
            add(Speck(p.x, p.y, matrix[i], matrix[i + 1]))
            i += 2
        }
    }
    override fun update() {
        super.update()
        if (countLiving() == 0) {
            killAndErase()
        }
    }
    override fun draw() {
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
        super.draw()
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
    }
    class Speck(x0: Float, y0: Float, mx: Int, my: Int) : PixelParticle() {
        init {
            color(COLOR)
            val x1 = x0 + mx * SIZE
            val y1 = y0 + my * SIZE
            val p = PointF().polar(Random.Float(2 * PointF.PI), 8f)
            val startX = x0 + p.x
            val startY = y0 + p.y
            val dx = x1 - startX
            val dy = y1 - startY
            x = startX
            y = startY
            speed.set(dx, dy)
            acc.set(-dx / 4, -dy / 4)
            lifespan = 2f
            left = lifespan
        }
        override fun update() {
            super.update()
            am = 1 - Math.abs(left / lifespan - 0.5f) * 2
            am *= am
            size(am * SIZE)
        }
        companion object {
            private const val COLOR = 0xFF4422
            private const val SIZE = 3
        }
    }
    companion object {
        private val WEAPON = intArrayOf(
            +2, -2,
            +1, -1,
            0, 0,
            -1, +1,
            -2, +2,
            -2, 0,
            0, +2
        )
        private val ARMOR = intArrayOf(
            -2, -1,
            -1, -1,
            +1, -1,
            +2, -1,
            -2, 0,
            -1, 0,
            0, 0,
            +1, 0,
            +2, 0,
            -1, +1,
            +1, +1,
            -1, +2,
            0, +2,
            +1, +2
        )
        private val RING = intArrayOf(
            0, -1,
            -1, 0,
            0, 0,
            +1, 0,
            -1, +1,
            +1, +1,
            -1, +2,
            0, +2,
            +1, +2
        )
        private val WAND = intArrayOf(
            +2, -2,
            +1, -1,
            0, 0,
            -1, +1,
            -2, +2,
            +1, -2,
            +2, -1
        )
        fun weapon(p: PointF): Degradation {
            return Degradation(p, WEAPON)
        }
        fun armor(p: PointF): Degradation {
            return Degradation(p, ARMOR)
        }
        fun ring(p: PointF): Degradation {
            return Degradation(p, RING)
        }
        fun wand(p: PointF): Degradation {
            return Degradation(p, WAND)
        }
    }
}
