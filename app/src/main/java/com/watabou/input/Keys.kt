package com.watabou.input
import android.view.KeyEvent
import com.watabou.utils.Signal
object Keys {
    const val BACK = KeyEvent.KEYCODE_BACK
    const val MENU = KeyEvent.KEYCODE_MENU
    const val VOLUME_UP = KeyEvent.KEYCODE_VOLUME_UP
    const val VOLUME_DOWN = KeyEvent.KEYCODE_VOLUME_DOWN
    val event = Signal<Key>(true)
    fun processTouchEvents(events: ArrayList<KeyEvent>) {
        val size = events.size
        for (i in 0 until size) {
            val e = events[i]
            when (e.action) {
                KeyEvent.ACTION_DOWN -> event.dispatch(Key(e.keyCode, true))
                KeyEvent.ACTION_UP -> event.dispatch(Key(e.keyCode, false))
            }
        }
    }
    class Key(val code: Int, val pressed: Boolean)
}
