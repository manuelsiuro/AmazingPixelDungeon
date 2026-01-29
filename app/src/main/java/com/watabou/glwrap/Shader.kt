package com.watabou.glwrap
import android.opengl.GLES20
class Shader(type: Int) {
    private val handle: Int = GLES20.glCreateShader(type)
    fun handle(): Int {
        return handle
    }
    fun source(src: String) {
        GLES20.glShaderSource(handle, src)
    }
    fun compile() {
        GLES20.glCompileShader(handle)
        val status = IntArray(1)
        GLES20.glGetShaderiv(handle, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            throw Error(GLES20.glGetShaderInfoLog(handle))
        }
    }
    fun delete() {
        GLES20.glDeleteShader(handle)
    }
    companion object {
        const val VERTEX = GLES20.GL_VERTEX_SHADER
        const val FRAGMENT = GLES20.GL_FRAGMENT_SHADER
        fun createCompiled(type: Int, src: String): Shader {
            val shader = Shader(type)
            shader.source(src)
            shader.compile()
            return shader
        }
    }
}
