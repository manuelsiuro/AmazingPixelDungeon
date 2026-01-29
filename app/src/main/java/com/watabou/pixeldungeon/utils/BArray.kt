package com.watabou.pixeldungeon.utils
object BArray {
    fun and(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        var res = result
        val length = a.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = a[i] && b[i]
        }
        return res
    }
    fun or(a: BooleanArray, b: BooleanArray, result: BooleanArray?): BooleanArray {
        var res = result
        val length = a.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = a[i] || b[i]
        }
        return res
    }
    fun not(a: BooleanArray, result: BooleanArray?): BooleanArray {
        var res = result
        val length = a.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = !a[i]
        }
        return res
    }
    fun `is`(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var res = result
        val length = a.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = (a[i] == v1)
        }
        return res
    }
    fun isOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var res = result
        val length = a.size
        val nv = v.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = false
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    res[i] = true
                    break
                }
            }
        }
        return res
    }
    fun isNot(a: IntArray, result: BooleanArray?, v1: Int): BooleanArray {
        var res = result
        val length = a.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = (a[i] != v1)
        }
        return res
    }
    fun isNotOneOf(a: IntArray, result: BooleanArray?, vararg v: Int): BooleanArray {
        var res = result
        val length = a.size
        val nv = v.size
        if (res == null) {
            res = BooleanArray(length)
        }
        for (i in 0 until length) {
            res[i] = true
            for (j in 0 until nv) {
                if (a[i] == v[j]) {
                    res[i] = false
                    break
                }
            }
        }
        return res
    }
}
