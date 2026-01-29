package com.watabou.utils
open class Point(var x: Int = 0, var y: Int = 0) : Cloneable {
    constructor(p: Point) : this(p.x, p.y)
    fun set(x: Int, y: Int): Point {
        this.x = x
        this.y = y
        return this
    }
    fun set(p: Point): Point {
        x = p.x
        y = p.y
        return this
    }
    public override fun clone(): Point {
        return Point(this)
    }
    fun scale(f: Float): Point {
        this.x = (this.x * f).toInt()
        this.y = (this.y * f).toInt()
        return this
    }
    fun offset(dx: Int, dy: Int): Point {
        x += dx
        y += dy
        return this
    }
    fun offset(d: Point): Point {
        x += d.x
        y += d.y
        return this
    }
    override fun equals(other: Any?): Boolean {
        if (other is Point) {
            return other.x == x && other.y == y
        } else {
            return false
        }
    }
}
