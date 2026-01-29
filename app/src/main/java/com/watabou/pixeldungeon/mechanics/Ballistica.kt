package com.watabou.pixeldungeon.mechanics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.levels.Level
import kotlin.math.abs
object Ballistica {
    var trace: IntArray = IntArray(Math.max(Level.WIDTH, Level.HEIGHT))
    var distance: Int = 0
    fun cast(from: Int, to: Int, magic: Boolean, hitChars: Boolean): Int {
        val w = Level.WIDTH
        val x0 = from % w
        val x1 = to % w
        val y0 = from / w
        val y1 = to / w
        var dx = x1 - x0
        var dy = y1 - y0
        val stepX = if (dx > 0) +1 else -1
        val stepY = if (dy > 0) +1 else -1
        dx = abs(dx)
        dy = abs(dy)
        val stepA: Int
        val stepB: Int
        val dA: Int
        val dB: Int
        if (dx > dy) {
            stepA = stepX
            stepB = stepY * w
            dA = dx
            dB = dy
        } else {
            stepA = stepY * w
            stepB = stepX
            dA = dy
            dB = dx
        }
        distance = 1
        trace[0] = from
        var cell = from
        var err = dA / 2
        while (cell != to || magic) {
            cell += stepA
            err += dB
            if (err >= dA) {
                err -= dA
                cell += stepB
            }
            trace[distance++] = cell
            if (!Level.passable[cell] && !Level.avoid[cell]) {
                return trace[--distance - 1]
            }
            if (Level.losBlocking[cell] || (hitChars && Actor.findChar(cell) != null)) {
                return cell
            }
        }
        trace[distance++] = cell
        return to
    }
}
