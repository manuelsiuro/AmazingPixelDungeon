package com.watabou.glwrap
import android.opengl.GLES20
import java.nio.FloatBuffer
class Attribute(private val location: Int) {
    fun location(): Int {
        return location
    }
    fun enable() {
        GLES20.glEnableVertexAttribArray(location)
    }
    fun disable() {
        GLES20.glDisableVertexAttribArray(location)
    }
    fun vertexPointer(size: Int, stride: Int, ptr: FloatBuffer) {
        GLES20.glVertexAttribPointer(location, size, GLES20.GL_FLOAT, false, stride * Float.SIZE_BYTES, ptr)
    }
}
