package com.watabou.input
import android.view.MotionEvent
import com.watabou.utils.PointF
import com.watabou.utils.Signal
object Touchscreen {
    val event = Signal<Touch?>(true)
    val pointers = HashMap<Int, Touch>()
    var x: Float = 0f
    var y: Float = 0f
    var touched: Boolean = false
    fun processTouchEvents(events: ArrayList<MotionEvent>) {
        val size = events.size
        for (i in 0 until size) {
            val e = events[i]
            val touch: Touch?
            when (e.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    touched = true
                    touch = Touch(e, 0)
                    pointers[e.getPointerId(0)] = touch
                    event.dispatch(touch)
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val index = e.actionIndex
                    touch = Touch(e, index)
                    pointers[e.getPointerId(index)] = touch
                    event.dispatch(touch)
                }
                MotionEvent.ACTION_MOVE -> {
                    val count = e.pointerCount
                    for (j in 0 until count) {
                        pointers[e.getPointerId(j)]?.update(e, j)
                    }
                    event.dispatch(null)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    event.dispatch(pointers.remove(e.getPointerId(e.actionIndex))?.up())
                }
                MotionEvent.ACTION_UP -> {
                    touched = false
                    event.dispatch(pointers.remove(e.getPointerId(0))?.up())
                }
            }
            e.recycle()
        }
    }
    class Touch(e: MotionEvent, index: Int) {
        val start: PointF
        val current: PointF
        var down: Boolean
        init {
            val x = e.getX(index)
            val y = e.getY(index)
            start = PointF(x, y)
            current = PointF(x, y)
            down = true
        }
        fun update(e: MotionEvent, index: Int) {
            current.set(e.getX(index), e.getY(index))
        }
        fun up(): Touch {
            down = false
            return this
        }
    }
}
