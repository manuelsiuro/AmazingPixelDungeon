package com.watabou.noosa
import android.opengl.GLES20
import com.watabou.glscripts.Script
import com.watabou.glwrap.Attribute
import com.watabou.glwrap.Quad
import com.watabou.glwrap.Uniform
import java.nio.FloatBuffer
import java.nio.ShortBuffer
class NoosaScript : Script() {
    var uCamera: Uniform
    var uModel: Uniform
    var uTex: Uniform
    var uColorM: Uniform
    var uColorA: Uniform
    var aXY: Attribute
    var aUV: Attribute
    private var lastCamera: Camera? = null
    init {
        compile(shader())
        uCamera = uniform("uCamera")
        uModel = uniform("uModel")
        uTex = uniform("uTex")
        uColorM = uniform("uColorM")
        uColorA = uniform("uColorA")
        aXY = attribute("aXYZW")
        aUV = attribute("aUV")
    }
    override fun use() {
        super.use()
        aXY.enable()
        aUV.enable()
    }
    fun drawElements(vertices: FloatBuffer, indices: ShortBuffer, size: Int) {
        vertices.position(0)
        aXY.vertexPointer(2, 4, vertices)
        vertices.position(2)
        aUV.vertexPointer(2, 4, vertices)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, size, GLES20.GL_UNSIGNED_SHORT, indices)
    }
    fun drawQuad(vertices: FloatBuffer) {
        vertices.position(0)
        aXY.vertexPointer(2, 4, vertices)
        vertices.position(2)
        aUV.vertexPointer(2, 4, vertices)
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, Quad.SIZE, GLES20.GL_UNSIGNED_SHORT, Quad.getIndices(1))
    }
    fun drawQuadSet(vertices: FloatBuffer, size: Int) {
        if (size == 0) {
            return
        }
        vertices.position(0)
        aXY.vertexPointer(2, 4, vertices)
        vertices.position(2)
        aUV.vertexPointer(2, 4, vertices)
        GLES20.glDrawElements(
            GLES20.GL_TRIANGLES,
            Quad.SIZE * size,
            GLES20.GL_UNSIGNED_SHORT,
            Quad.getIndices(size)
        )
    }
    fun lighting(rm: Float, gm: Float, bm: Float, am: Float, ra: Float, ga: Float, ba: Float, aa: Float) {
        uColorM.value4f(rm, gm, bm, am)
        uColorA.value4f(ra, ga, ba, aa)
    }
    fun resetCamera() {
        lastCamera = null
    }
    fun camera(camera: Camera?) {
        var cam = camera
        if (cam == null) {
            cam = Camera.main
        }
        if (cam !== lastCamera) {
            lastCamera = cam
            uCamera.valueM4(cam!!.matrix)
            GLES20.glScissor(
                cam.x,
                Game.height - cam.screenHeight - cam.y,
                cam.screenWidth,
                cam.screenHeight
            )
        }
    }
    protected fun shader(): String {
        return SHADER
    }
    companion object {
        fun get(): NoosaScript {
            return Script.use(NoosaScript::class.java)
        }
        private const val SHADER =
            "uniform mat4 uCamera;" +
                    "uniform mat4 uModel;" +
                    "attribute vec4 aXYZW;" +
                    "attribute vec2 aUV;" +
                    "varying vec2 vUV;" +
                    "void main() {" +
                    "  gl_Position = uCamera * uModel * aXYZW;" +
                    "  vUV = aUV;" +
                    "}" +
                    "//\n" +
                    "precision mediump float;" +
                    "varying vec2 vUV;" +
                    "uniform sampler2D uTex;" +
                    "uniform vec4 uColorM;" +
                    "uniform vec4 uColorA;" +
                    "void main() {" +
                    "  gl_FragColor = texture2D( uTex, vUV ) * uColorM + uColorA;" +
                    "}"
    }
}
