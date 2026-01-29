package com.watabou.utils
object ColorMath {
    fun interpolate(A: Int, B: Int, p: Float): Int {
        if (p <= 0) {
            return A
        } else if (p >= 1) {
            return B
        }
        val ra = A shr 16
        val ga = (A shr 8) and 0xFF
        val ba = A and 0xFF
        val rb = B shr 16
        val gb = (B shr 8) and 0xFF
        val bb = B and 0xFF
        val p1 = 1 - p
        val r = (p1 * ra + p * rb).toInt()
        val g = (p1 * ga + p * gb).toInt()
        val b = (p1 * ba + p * bb).toInt()
        return (r shl 16) + (g shl 8) + b
    }
    fun interpolate(p: Float, vararg colors: Int): Int {
        if (p <= 0) {
            return colors[0]
        } else if (p >= 1) {
            return colors[colors.size - 1]
        }
        val segment = (colors.size * p).toInt()
        return interpolate(colors[segment], colors[segment + 1], (p * (colors.size - 1)) % 1)
    }
    fun random(a: Int, b: Int): Int {
        return interpolate(a, b, Random.Float())
    }
}
