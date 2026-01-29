package com.watabou.utils
import java.util.HashMap
import kotlin.math.floor
object Random {
    fun Float(min: Float, max: Float): Float {
        return (min + Math.random() * (max - min)).toFloat()
    }
    fun Float(max: Float): Float {
        return (Math.random() * max).toFloat()
    }
    fun Float(): Float {
        return Math.random().toFloat()
    }
    fun Int(max: Int): Int {
        return if (max > 0) floor(Math.random() * max).toInt() else 0
    }
    fun Int(min: Int, max: Int): Int {
        return min + floor(Math.random() * (max - min)).toInt()
    }
    fun IntRange(min: Int, max: Int): Int {
        return min + floor(Math.random() * (max - min + 1)).toInt()
    }
    fun NormalIntRange(min: Int, max: Int): Int {
        return min + ((Math.random() + Math.random()) * (max - min + 1) / 2f).toInt()
    }
    fun chances(chances: FloatArray): Int {
        val length = chances.size
        var sum = chances[0]
        for (i in 1 until length) {
            sum += chances[i]
        }
        val value = Float(sum)
        sum = chances[0]
        for (i in 0 until length) {
            if (value < sum) {
                return i
            }
            sum += chances[i + 1]
        }
        return 0
    }
    fun <K> chances(chances: HashMap<K, Float>): K? {
        val size = chances.size
        val values = chances.keys.toList()
        val probs = FloatArray(size)
        var sum = 0f
        for (i in 0 until size) {
            probs[i] = chances[values[i]] ?: 0f
            sum += probs[i]
        }
        val value = Float(sum)
        sum = probs[0]
        for (i in 0 until size) {
            if (value < sum) {
                return values[i]
            }
            sum += probs[i + 1]
        }
        return null
    }
    fun index(collection: Collection<*>): Int {
        return (Math.random() * collection.size).toInt()
    }
    @SafeVarargs
    fun <T> oneOf(vararg array: T): T {
        return array[(Math.random() * array.size).toInt()]
    }
    fun <T> element(array: Array<T>): T {
        return element(array, array.size)
    }
    fun <T> element(array: Array<T>, max: Int): T {
        return array[(Math.random() * max).toInt()]
    }
    fun <T> element(collection: Collection<T>): T? {
        val size = collection.size
        return if (size > 0) collection.elementAt(Int(size)) else null
    }
    fun <T> shuffle(array: Array<T>) {
        for (i in 0 until array.size - 1) {
            val j = Int(i, array.size)
            if (j != i) {
                val t = array[i]
                array[i] = array[j]
                array[j] = t
            }
        }
    }
    fun <U, V> shuffle(u: Array<U>, v: Array<V>) {
        for (i in 0 until u.size - 1) {
            val j = Int(i, u.size)
            if (j != i) {
                val ut = u[i]
                u[i] = u[j]
                u[j] = ut
                val vt = v[i]
                v[i] = v[j]
                v[j] = vt
            }
        }
    }
}
