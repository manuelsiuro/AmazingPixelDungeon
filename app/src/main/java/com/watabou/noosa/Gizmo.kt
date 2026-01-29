package com.watabou.noosa
open class Gizmo {
    var exists: Boolean = true
    var alive: Boolean = true
    var active: Boolean = true
    var visible: Boolean = true
    var parent: Group? = null
    var camera: Camera? = null
    open fun destroy() {
        parent = null
    }
    open fun update() {}
    open fun draw() {}
    open fun kill() {
        alive = false
        exists = false
    }
    // Not exactly opposite to "kill" method
    open fun revive() {
        alive = true
        exists = true
    }
    open fun camera(): Camera? {
        if (camera != null) {
            return camera
        } else if (parent != null) {
            return parent!!.camera()
        } else {
            return null
        }
    }
    open fun isVisible(): Boolean {
        return if (parent == null) {
            visible
        } else {
            visible && parent!!.isVisible()
        }
    }
    fun isActive(): Boolean {
        return if (parent == null) {
            active
        } else {
            active && parent!!.isActive()
        }
    }
    fun killAndErase() {
        kill()
        if (parent != null) {
            parent!!.erase(this)
        }
    }
    fun remove() {
        if (parent != null) {
            parent!!.remove(this)
        }
    }
}
