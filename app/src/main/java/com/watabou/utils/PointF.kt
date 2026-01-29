package com.watabou.utils
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
open class PointF(
    var x: Float = 0f,
    var y: Float = 0f
) {
    constructor(p: PointF) : this(p.x, p.y)
    constructor(p: Point) : this(p.x.toFloat(), p.y.toFloat())
    open fun clone(): PointF {
        return PointF(this)
    }
    fun scale(f: Float): PointF {
        this.x *= f
        this.y *= f
        return this
    }
    fun invScale(f: Float): PointF {
        this.x /= f
        this.y /= f
        return this
    }
    fun set(x: Float, y: Float): PointF {
        this.x = x
        this.y = y
        return this
    }
    fun set(p: PointF): PointF {
        this.x = p.x
        this.y = p.y
        return this
    }
    fun set(v: Float): PointF {
        this.x = v
        this.y = v
        return this
    }
    fun polar(a: Float, l: Float): PointF {
        this.x = (l * cos(a.toDouble())).toFloat()
        this.y = (l * sin(a.toDouble())).toFloat()
        return this
    }
    fun offset(dx: Float, dy: Float): PointF {
        x += dx
        y += dy
        return this
    }
    fun offset(p: PointF): PointF {
        x += p.x
        y += p.y
        return this
    }
    fun negate(): PointF {
        x = -x
        y = -y
        return this
    }
    fun normalize(): PointF {
        val l = length()
        x /= l
        y /= l
        return this
    }
    fun floor(): Point {
        return Point(x.toInt(), y.toInt())
    }
    fun length(): Float {
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }
    fun fast(): PointF {
        return PointF(x.toInt().toFloat(), y.toInt().toFloat())
    }
    override fun toString(): String {
        return "$x, $y"
    }
    companion object {
        const val PI: Float = 3.1415926f
        const val PI2: Float = PI * 2
        const val G2R: Float = PI / 180
        fun polar(a: Float): PointF {
            return PointF(cos(a.toDouble()).toFloat(), sin(a.toDouble()).toFloat())
        }
        fun sum(a: PointF, b: PointF): PointF {
            return PointF(a.x + b.x, a.y + b.y)
        }
        fun diff(a: PointF, b: PointF): PointF {
            return PointF(a.x - b.x, a.y - b.y)
        }
        fun inter(a: PointF, b: PointF, d: Float): PointF {
            return PointF(a.x + (b.x - a.x) * d, a.y + (b.y - a.y) * d)
        }
        fun distance(a: PointF, b: PointF): Float {
            val dx = a.x - b.x
            val dy = a.y - b.y
            return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }
        fun distance(p: PointF): Float {
            return sqrt((p.x * p.x + p.y * p.y).toDouble()).toFloat()
        }
        fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
            val dx = x1 - x2
            val dy = y1 - y2
            return sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        }
        fun angle(start: PointF, end: PointF): Float {
            return atan2((end.y - start.y).toDouble(), (end.x - start.x).toDouble()).toFloat()
        }
    }
}
