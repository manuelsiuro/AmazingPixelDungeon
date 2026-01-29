package com.watabou.utils
object SystemTime {
    var now: Long = 0
    fun tick() {
        now = System.currentTimeMillis()
    }
}
