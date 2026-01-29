package com.watabou.pixeldungeon.mechanics
import com.watabou.pixeldungeon.levels.Level
import java.util.Arrays
object ShadowCaster {
    private const val MAX_DISTANCE = 8
    private const val WIDTH = Level.WIDTH
    private const val HEIGHT = Level.HEIGHT
    private var distance: Int = 0
    private var limits: IntArray? = null
    private var losBlocking: BooleanArray? = null
    private var fieldOfView: BooleanArray? = null
    private val rounding: Array<IntArray> = Array(MAX_DISTANCE + 1) { IntArray(0) }
    init {
        for (i in 1..MAX_DISTANCE) {
            rounding[i] = IntArray(i + 1)
            for (j in 1..i) {
                rounding[i][j] = Math.min(j.toDouble(), Math.round(i * Math.cos(Math.asin(j / (i + 0.5)))).toDouble()).toInt()
            }
        }
    }
    private val obs = Obstacles()
    fun castShadow(x: Int, y: Int, fieldOfView: BooleanArray, distance: Int) {
        losBlocking = Level.losBlocking
        ShadowCaster.distance = distance
        limits = rounding[distance]
        ShadowCaster.fieldOfView = fieldOfView
        Arrays.fill(fieldOfView, false)
        fieldOfView[y * WIDTH + x] = true
        scanSector(x, y, +1, +1, 0, 0)
        scanSector(x, y, -1, +1, 0, 0)
        scanSector(x, y, +1, -1, 0, 0)
        scanSector(x, y, -1, -1, 0, 0)
        scanSector(x, y, 0, 0, +1, +1)
        scanSector(x, y, 0, 0, -1, +1)
        scanSector(x, y, 0, 0, +1, -1)
        scanSector(x, y, 0, 0, -1, -1)
    }
    private fun scanSector(cx: Int, cy: Int, m1: Int, m2: Int, m3: Int, m4: Int) {
        obs.reset()
        val currentLimits = limits ?: return
        val currentLosBlocking = losBlocking ?: return
        val currentFieldOfView = fieldOfView ?: return
        for (p in 1..distance) {
            val dq2 = 0.5f / p
            val pp = currentLimits[p]
            for (q in 0..pp) {
                val x = cx + q * m1 + p * m3
                val y = cy + p * m2 + q * m4
                if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
                    val a0 = q.toFloat() / p
                    val a1 = a0 - dq2
                    val a2 = a0 + dq2
                    val pos = y * WIDTH + x
                    if (obs.isBlocked(a0) && obs.isBlocked(a1) && obs.isBlocked(a2)) {
                        // Do nothing
                    } else {
                        currentFieldOfView[pos] = true
                    }
                    if (currentLosBlocking[pos]) {
                        obs.add(a1, a2)
                    }
                }
            }
            obs.nextRow()
        }
    }
    private class Obstacles {
        private val SIZE = (MAX_DISTANCE + 1) * (MAX_DISTANCE + 1) / 2
        private val a1 = FloatArray(SIZE)
        private val a2 = FloatArray(SIZE)
        private var length: Int = 0
        private var limit: Int = 0
        fun reset() {
            length = 0
            limit = 0
        }
        fun add(o1: Float, o2: Float) {
            if (length > limit && o1 <= a2[length - 1]) {
                // Merging several blocking cells
                a2[length - 1] = o2
            } else {
                a1[length] = o1
                a2[length++] = o2
            }
        }
        fun isBlocked(a: Float): Boolean {
            for (i in 0 until limit) {
                if (a >= a1[i] && a <= a2[i]) {
                    return true
                }
            }
            return false
        }
        fun nextRow() {
            limit = length
        }
    }
}
