package com.watabou.noosa
import com.watabou.input.Keys
import com.watabou.utils.Signal
open class Scene : Group() {
    private var keyListener: Signal.Listener<Keys.Key>? = null
    open fun create() {
        keyListener = object : Signal.Listener<Keys.Key> {
            override fun onSignal(t: Keys.Key?) {
                if (Game.instance != null && t != null && t.pressed) {
                    when (t.code) {
                        Keys.BACK -> onBackPressed()
                        Keys.MENU -> onMenuPressed()
                    }
                }
            }
        }
        Keys.event.add(keyListener!!)
    }
    override fun destroy() {
        Keys.event.remove(keyListener!!)
        super.destroy()
    }
    open fun pause() {}
    open fun resume() {}
    override fun update() {
        super.update()
    }
    override fun camera(): Camera? {
        return Camera.main
    }
    protected open fun onBackPressed() {
        Game.instance?.finish()
    }
    protected open fun onMenuPressed() {}
}
