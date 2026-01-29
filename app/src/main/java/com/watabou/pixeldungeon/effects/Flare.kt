package com.watabou.pixeldungeon.effects
import android.opengl.GLES20
import com.watabou.gltextures.Gradient
import com.watabou.gltextures.SmartTexture
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.NoosaScript
import com.watabou.noosa.Visual
import com.watabou.utils.PointF
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
class Flare(nRays: Int, radius: Float) : Visual(0f, 0f, 0f, 0f) {
    private var duration: Float = 0f
    private var lifespan: Float = 0f
    private var lightMode: Boolean = true
    private var texture: SmartTexture
    private var vertices: FloatBuffer
    private var indices: ShortBuffer
    private var nRays: Int
    init {
        val gradient = intArrayOf(0xFFFFFFFF.toInt(), 0x00FFFFFF)
        texture = Gradient(gradient)
        this.nRays = nRays
        angle = 45f
        angularSpeed = 180f
        vertices = ByteBuffer
            .allocateDirect((nRays * 2 + 1) * 4 * (Float.SIZE_BYTES))
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        indices = ByteBuffer
            .allocateDirect(nRays * 3 * (Short.SIZE_BYTES))
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
        val v = FloatArray(4)
        v[0] = 0f
        v[1] = 0f
        v[2] = 0.25f
        v[3] = 0f
        vertices.put(v)
        v[2] = 0.75f
        v[3] = 0f
        for (i in 0 until nRays) {
            var a = i * 3.1415926f * 2 / nRays
            v[0] = Math.cos(a.toDouble()).toFloat() * radius
            v[1] = Math.sin(a.toDouble()).toFloat() * radius
            vertices.put(v)
            a += 3.1415926f * 2 / nRays / 2
            v[0] = Math.cos(a.toDouble()).toFloat() * radius
            v[1] = Math.sin(a.toDouble()).toFloat() * radius
            vertices.put(v)
            indices.put(0.toShort())
            indices.put((1 + i * 2).toShort())
            indices.put((2 + i * 2).toShort())
        }
        indices.position(0)
    }
    fun color(color: Int, lightMode: Boolean): Flare {
        this.lightMode = lightMode
        hardlight(color)
        return this
    }
    fun show(visual: Visual, duration: Float): Flare {
        point(visual.center())
        val parent = requireNotNull(visual.parent) { "visual.parent must not be null" }
        parent.addToBack(this)
        lifespan = duration
        this.duration = duration
        return this
    }
    fun show(parent: Group, pos: PointF, duration: Float): Flare {
        point(pos)
        parent.add(this)
        lifespan = duration
        this.duration = duration
        return this
    }
    override fun update() {
        super.update()
        if (duration > 0) {
            lifespan -= Game.elapsed
            if (lifespan > 0) {
                var p = 1 - lifespan / duration // 0 -> 1
                p = if (p < 0.25f) p * 4 else (1 - p) * 1.333f
                scale.set(p)
                alpha(p)
            } else {
                killAndErase()
            }
        }
    }
    override fun draw() {
        super.draw()
        if (lightMode) {
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
            drawRays()
            GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        } else {
            drawRays()
        }
    }
    private fun drawRays() {
        val script = NoosaScript.get()
        texture.bind()
        script.uModel.valueM4(matrix)
        script.lighting(
            rm, gm, bm, am,
            ra, ga, ba, aa
        )
        script.camera(camera)
        script.drawElements(vertices, indices, nRays * 3)
    }
}
