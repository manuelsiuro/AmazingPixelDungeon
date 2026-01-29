package com.watabou.noosa.tweeners
import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo
abstract class Tweener(target: Gizmo?, interval: Float) : Gizmo() {
    var target: Gizmo? = target
    var interval: Float = interval
    var elapsed: Float = 0f
    var listener: Listener? = null
    override fun update() {
        elapsed += Game.elapsed
        if (elapsed >= interval) {
            updateValues(1f)
            onComplete()
            kill()
        } else {
            updateValues(elapsed / interval)
        }
    }
    protected open fun onComplete() {
        if (listener != null) {
            listener!!.onComplete(this)
        }
    }
    protected abstract fun updateValues(progress: Float)
    interface Listener {
        fun onComplete(tweener: Tweener)
    }
}
