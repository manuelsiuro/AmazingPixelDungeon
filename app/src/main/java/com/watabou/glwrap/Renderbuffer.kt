package com.watabou.glwrap
import android.opengl.GLES20
class Renderbuffer {
    private val id: Int
    init {
        val buffers = IntArray(1)
        GLES20.glGenRenderbuffers(1, buffers, 0)
        id = buffers[0]
    }
    fun id(): Int {
        return id
    }
    fun bind() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, id)
    }
    fun delete() {
        val buffers = intArrayOf(id)
        GLES20.glDeleteRenderbuffers(1, buffers, 0)
    }
    fun storage(format: Int, width: Int, height: Int) {
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, format, width, height)
    }
    companion object {
        const val RGBA8 = GLES20.GL_RGBA    // ?
        const val DEPTH16 = GLES20.GL_DEPTH_COMPONENT16
        const val STENCIL8 = GLES20.GL_STENCIL_INDEX8
    }
}
