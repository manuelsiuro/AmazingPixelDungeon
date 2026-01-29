package com.watabou.glwrap
import kotlin.math.tan
import kotlin.math.sin
import kotlin.math.cos
object Matrix {
    const val G2RAD = 0.01745329251994329576923690768489f
    fun clone(m: FloatArray): FloatArray {
        var n = m.size
        val res = FloatArray(n)
        do {
            res[--n] = m[n]
        } while (n > 0)
        return res
    }
    fun copy(src: FloatArray, dst: FloatArray) {
        var n = src.size
        do {
            dst[--n] = src[n]
        } while (n > 0)
    }
    fun setIdentity(m: FloatArray) {
        for (i in 0..15) {
            m[i] = 0f
        }
        for (i in 0..15 step 5) {
            m[i] = 1f
        }
    }
    fun rotate(m: FloatArray, a: Float) {
        var ang = a
        ang *= G2RAD
        val sin = sin(ang.toDouble()).toFloat()
        val cos = cos(ang.toDouble()).toFloat()
        val m0 = m[0]
        val m1 = m[1]
        val m4 = m[4]
        val m5 = m[5]
        m[0] = m0 * cos + m4 * sin
        m[1] = m1 * cos + m5 * sin
        m[4] = -m0 * sin + m4 * cos
        m[5] = -m1 * sin + m5 * cos
    }
    fun skewX(m: FloatArray, a: Float) {
        val t = tan((a * G2RAD).toDouble()).toFloat()
        m[4] += -m[0] * t
        m[5] += -m[1] * t
    }
    fun skewY(m: FloatArray, a: Float) {
        val t = tan((a * G2RAD).toDouble()).toFloat()
        m[0] += m[4] * t
        m[1] += m[5] * t
    }
    fun scale(m: FloatArray, x: Float, y: Float) {
        m[0] *= x
        m[1] *= x
        m[2] *= x
        m[3] *= x
        m[4] *= y
        m[5] *= y
        m[6] *= y
        m[7] *= y
        //	android.opengl.Matrix.scaleM( m, 0, x, y, 1 );
    }
    fun translate(m: FloatArray, x: Float, y: Float) {
        m[12] += m[0] * x + m[4] * y
        m[13] += m[1] * x + m[5] * y
    }
    fun multiply(left: FloatArray, right: FloatArray, result: FloatArray) {
        android.opengl.Matrix.multiplyMM(result, 0, left, 0, right, 0)
    }
}
