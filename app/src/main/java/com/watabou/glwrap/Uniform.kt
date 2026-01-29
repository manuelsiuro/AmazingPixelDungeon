package com.watabou.glwrap
import android.opengl.GLES20
class Uniform(private val location: Int) {
    fun location(): Int {
        return location
    }
    fun enable() {
        GLES20.glEnableVertexAttribArray(location)
    }
    fun disable() {
        GLES20.glDisableVertexAttribArray(location)
    }
    fun value(value: Int) {
        GLES20.glUniform1i(location, value)
    }
    fun value1f(value: Float) {
        GLES20.glUniform1f(location, value)
    }
    fun value2f(v1: Float, v2: Float) {
        GLES20.glUniform2f(location, v1, v2)
    }
    fun value4f(v1: Float, v2: Float, v3: Float, v4: Float) {
        GLES20.glUniform4f(location, v1, v2, v3, v4)
    }
    fun valueM3(value: FloatArray) {
        GLES20.glUniformMatrix3fv(location, 1, false, value, 0)
    }
    fun valueM4(value: FloatArray) {
        GLES20.glUniformMatrix4fv(location, 1, false, value, 0)
    }
}
