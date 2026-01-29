package com.watabou.noosa
import java.util.ArrayList
open class Group : Gizmo() {
    protected var members: ArrayList<Gizmo?> = ArrayList()
    // Accessing it is a little faster,
    // than calling memebers.getSize()
    var length: Int = 0
    override fun destroy() {
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null) {
                g.destroy()
            }
            i++
        }
        members.clear()
        length = 0
    }
    override fun update() {
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && g.exists && g.active) {
                g.update()
            }
            i++
        }
    }
    override fun draw() {
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && g.exists && g.visible) {
                g.draw()
            }
            i++
        }
    }
    override fun kill() {
        // A killed group keeps all its members,
        // but they get killed too
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && g.exists) {
                g.kill()
            }
            i++
        }
        super.kill()
    }
    fun indexOf(g: Gizmo): Int {
        return members.indexOf(g)
    }
    fun add(g: Gizmo): Gizmo {
        if (g.parent == this) {
            return g
        }
        if (g.parent != null) {
            g.parent!!.remove(g)
        }
        // Trying to find an empty space for a new member
        for (i in 0 until length) {
            if (members[i] == null) {
                members[i] = g
                g.parent = this
                return g
            }
        }
        members.add(g)
        g.parent = this
        length++
        return g
    }
    fun addToBack(g: Gizmo): Gizmo {
        if (g.parent == this) {
            sendToBack(g)
            return g
        }
        if (g.parent != null) {
            g.parent!!.remove(g)
        }
        if (members.isNotEmpty() && members[0] == null) {
            members[0] = g
            g.parent = this
            return g
        }
        members.add(0, g)
        g.parent = this
        length++
        return g
    }
    fun recycle(c: Class<out Gizmo>?): Gizmo? {
        val g = getFirstAvailable(c)
        if (g != null) {
            return g
        } else if (c == null) {
            return null
        } else {
            try {
                return add(c.getDeclaredConstructor().newInstance())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
    // Fast removal - replacing with null
    fun erase(g: Gizmo): Gizmo? {
        val index = members.indexOf(g)
        if (index != -1) {
            members[index] = null
            g.parent = null
            return g
        } else {
            return null
        }
    }
    // Real removal
    fun remove(g: Gizmo): Gizmo? {
        if (members.remove(g)) {
            length--
            g.parent = null
            return g
        } else {
            return null
        }
    }
    fun replace(oldOne: Gizmo, newOne: Gizmo): Gizmo? {
        val index = members.indexOf(oldOne)
        if (index != -1) {
            members[index] = newOne
            newOne.parent = this
            oldOne.parent = null
            return newOne
        } else {
            return null
        }
    }
    fun getFirstAvailable(c: Class<out Gizmo>?): Gizmo? {
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && !g.exists && ((c == null) || g.javaClass == c)) {
                return g
            }
            i++
        }
        return null
    }
    fun countLiving(): Int {
        var count = 0
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && g.exists && g.alive) {
                count++
            }
            i++
        }
        return count
    }
    fun countDead(): Int {
        var count = 0
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null && !g.alive) {
                count++
            }
            i++
        }
        return count
    }
    fun random(): Gizmo? {
        return if (length > 0) {
            members[(Math.random() * length).toInt()]
        } else {
            null
        }
    }
    fun clear() {
        var i = 0
        while (i < length) {
            val g = members[i]
            if (g != null) {
                g.parent = null
            }
            i++
        }
        members.clear()
        length = 0
    }
    fun bringToFront(g: Gizmo): Gizmo? {
        if (members.contains(g)) {
            members.remove(g)
            members.add(g)
            return g
        } else {
            return null
        }
    }
    fun sendToBack(g: Gizmo): Gizmo? {
        if (members.contains(g)) {
            members.remove(g)
            members.add(0, g)
            return g
        } else {
            return null
        }
    }
}
