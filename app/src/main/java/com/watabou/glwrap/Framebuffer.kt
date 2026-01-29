package com.watabou.glwrap
import android.opengl.GLES20
class Framebuffer {
    private var id: Int = 0
    constructor() {
        val buffers = IntArray(1)
        GLES20.glGenBuffers(1, buffers, 0)
        id = buffers[0]
    }
    private constructor(@Suppress("UNUSED_PARAMETER") n: Int)
    fun bind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, id)
    }
    fun delete() {
        val buffers = intArrayOf(id)
        GLES20.glDeleteFramebuffers(1, buffers, 0)
    }
    fun attach(point: Int, tex: Texture) {
        bind()
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, point, GLES20.GL_TEXTURE_2D, tex.id, 0)
    }
    fun attach(point: Int, buffer: Renderbuffer) {
        bind()
        GLES20.glFramebufferRenderbuffer(GLES20.GL_RENDERBUFFER, point, GLES20.GL_TEXTURE_2D, buffer.id())
    }
    fun status(): Boolean {
        bind()
        return GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) == GLES20.GL_FRAMEBUFFER_COMPLETE
    }
    companion object {
        const val COLOR = GLES20.GL_COLOR_ATTACHMENT0
        const val DEPTH = GLES20.GL_DEPTH_ATTACHMENT
        const val STENCIL = GLES20.GL_STENCIL_ATTACHMENT
        val system = Framebuffer(0)
    }
}
