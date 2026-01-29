package com.watabou.glwrap
import android.opengl.GLES20
open class Program {
    private val handle: Int = GLES20.glCreateProgram()
    fun handle(): Int {
        return handle
    }
    fun attach(shader: Shader) {
        GLES20.glAttachShader(handle, shader.handle())
    }
    fun link() {
        GLES20.glLinkProgram(handle)
        val status = IntArray(1)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == GLES20.GL_FALSE) {
            throw Error(GLES20.glGetProgramInfoLog(handle))
        }
    }
    fun attribute(name: String): Attribute {
        return Attribute(GLES20.glGetAttribLocation(handle, name))
    }
    fun uniform(name: String): Uniform {
        return Uniform(GLES20.glGetUniformLocation(handle, name))
    }
    open fun use() {
        GLES20.glUseProgram(handle)
    }
    open fun delete() {
        GLES20.glDeleteProgram(handle)
    }
    companion object {
        fun create(vararg shaders: Shader): Program {
            val program = Program()
            for (i in shaders.indices) {
                program.attach(shaders[i])
            }
            program.link()
            return program
        }
    }
}
