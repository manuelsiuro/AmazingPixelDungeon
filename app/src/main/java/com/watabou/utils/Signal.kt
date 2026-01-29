package com.watabou.utils
import java.util.LinkedList
class Signal<T> @JvmOverloads constructor(private val stackMode: Boolean = false) {
    private val listeners = LinkedList<Listener<T>>()
    private var canceled: Boolean = false
    fun add(listener: Listener<T>) {
        if (!listeners.contains(listener)) {
            if (stackMode) {
                listeners.addFirst(listener)
            } else {
                listeners.addLast(listener)
            }
        }
    }
    fun remove(listener: Listener<T>) {
        listeners.remove(listener)
    }
    fun removeAll() {
        listeners.clear()
    }
    fun replace(listener: Listener<T>) {
        removeAll()
        add(listener)
    }
    fun numListeners(): Int {
        return listeners.size
    }
    fun dispatch(t: T) {
        val list = listeners.toTypedArray()
        canceled = false
        for (i in list.indices) {
            val listener = list[i]
            if (listeners.contains(listener)) {
                listener.onSignal(t)
                if (canceled) {
                    return
                }
            }
        }
    }
    fun cancel() {
        canceled = true
    }
    interface Listener<T> {
        fun onSignal(t: T?)
    }
}
