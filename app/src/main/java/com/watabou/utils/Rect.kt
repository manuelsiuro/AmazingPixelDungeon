package com.watabou.utils
import kotlin.math.max
import kotlin.math.min
open class Rect(
    var left: Int = 0,
    var top: Int = 0,
    var right: Int = 0,
    var bottom: Int = 0
) {
    constructor(rect: Rect) : this(rect.left, rect.top, rect.right, rect.bottom)
    fun width(): Int {
        return right - left
    }
    fun height(): Int {
        return bottom - top
    }
    fun square(): Int {
        return (right - left) * (bottom - top)
    }
    fun set(left: Int, top: Int, right: Int, bottom: Int): Rect {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        return this
    }
    fun set(rect: Rect): Rect {
        return set(rect.left, rect.top, rect.right, rect.bottom)
    }
    fun isEmpty(): Boolean {
        return right <= left || bottom <= top
    }
    fun setEmpty(): Rect {
        bottom = 0
        top = 0
        right = 0
        left = 0
        return this
    }
    fun intersect(other: Rect): Rect {
        val result = Rect()
        result.left = max(left.toDouble(), other.left.toDouble()).toInt()
        result.right = min(right.toDouble(), other.right.toDouble()).toInt()
        result.top = max(top.toDouble(), other.top.toDouble()).toInt()
        result.bottom = min(bottom.toDouble(), other.bottom.toDouble()).toInt()
        return result
    }
    fun union(x: Int, y: Int): Rect {
        if (isEmpty()) {
            return set(x, y, x + 1, y + 1)
        } else {
            if (x < left) {
                left = x
            } else if (x >= right) {
                right = x + 1
            }
            if (y < top) {
                top = y
            } else if (y >= bottom) {
                bottom = y + 1
            }
            return this
        }
    }
    fun union(p: Point): Rect {
        return union(p.x, p.y)
    }
    fun inside(p: Point): Boolean {
        return p.x >= left && p.x < right && p.y >= top && p.y < bottom
    }
    fun shrink(d: Int): Rect {
        return Rect(left + d, top + d, right - d, bottom - d)
    }
    fun shrink(): Rect {
        return shrink(1)
    }
}
