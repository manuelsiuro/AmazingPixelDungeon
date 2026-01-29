package com.watabou.utils
import java.util.ArrayList
open class SparseArray<T> : android.util.SparseArray<T>() {
    fun keyArray(): IntArray {
        val size = size()
        val array = IntArray(size)
        for (i in 0 until size) {
            array[i] = keyAt(i)
        }
        return array
    }
    fun values(): ArrayList<T> {
        val size = size()
        val list = ArrayList<T>(size)
        for (i in 0 until size) {
            list.add(valueAt(i))
        }
        return list
    }
}
