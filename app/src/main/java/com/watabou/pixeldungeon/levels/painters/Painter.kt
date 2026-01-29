package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.utils.Point
import com.watabou.utils.Rect
import java.util.Arrays
abstract class Painter {
    companion object {
        fun set(level: Level, cell: Int, value: Int) {
            level.map[cell] = value
        }
        fun set(level: Level, x: Int, y: Int, value: Int) {
            set(level, x + y * Level.WIDTH, value)
        }
        fun set(level: Level, p: Point, value: Int) {
            set(level, p.x, p.y, value)
        }
        fun fill(level: Level, x: Int, y: Int, w: Int, h: Int, value: Int) {
            val width = Level.WIDTH
            var pos = y * width + x
            for (i in y until y + h) {
                Arrays.fill(level.map, pos, pos + w, value)
                pos += width
            }
        }
        fun fill(level: Level, rect: Rect, value: Int) {
            fill(level, rect.left, rect.top, rect.width() + 1, rect.height() + 1, value)
        }
        fun fill(level: Level, rect: Rect, m: Int, value: Int) {
            fill(level, rect.left + m, rect.top + m, rect.width() + 1 - m * 2, rect.height() + 1 - m * 2, value)
        }
        fun fill(level: Level, rect: Rect, l: Int, t: Int, r: Int, b: Int, value: Int) {
            fill(
                level,
                rect.left + l,
                rect.top + t,
                rect.width() + 1 - (l + r),
                rect.height() + 1 - (t + b),
                value
            )
        }
        fun drawInside(level: Level, room: Room, from: Point, n: Int, value: Int): Point {
            val step = Point()
            if (from.x == room.left) {
                step.set(+1, 0)
            } else if (from.x == room.right) {
                step.set(-1, 0)
            } else if (from.y == room.top) {
                step.set(0, +1)
            } else if (from.y == room.bottom) {
                step.set(0, -1)
            }
            val p = Point(from).offset(step)
            for (i in 0 until n) {
                if (value != -1) {
                    set(level, p, value)
                }
                p.offset(step)
            }
            return p
        }
    }
}
