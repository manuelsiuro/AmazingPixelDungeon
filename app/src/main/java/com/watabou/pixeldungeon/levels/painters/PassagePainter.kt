package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.utils.Point
import java.util.ArrayList
import java.util.Collections
object PassagePainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        val pasWidth = room.width() - 2
        val pasHeight = room.height() - 2
        val floor = level.tunnelTile()
        val joints = ArrayList<Int>()
        for (door in room.connected.values) {
            val doorPos = door ?: continue
            joints.add(xy2p(room, doorPos, pasWidth, pasHeight))
        }
        Collections.sort(joints)
        val nJoints = joints.size
        val perimeter = pasWidth * 2 + pasHeight * 2
        var start = 0
        var maxD = joints[0] + perimeter - joints[nJoints - 1]
        for (i in 1 until nJoints) {
            val d = joints[i] - joints[i - 1]
            if (d > maxD) {
                maxD = d
                start = i
            }
        }
        val end = (start + nJoints - 1) % nJoints
        var p = joints[start]
        do {
            Painter.set(level, p2xy(room, p, pasWidth, pasHeight), floor)
            p = (p + 1) % perimeter
        } while (p != joints[end])
        Painter.set(level, p2xy(room, p, pasWidth, pasHeight), floor)
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.TUNNEL)
        }
    }
    private fun xy2p(room: Room, xy: Point, pasWidth: Int, pasHeight: Int): Int {
        if (xy.y == room.top) {
            return (xy.x - room.left - 1)
        } else if (xy.x == room.right) {
            return (xy.y - room.top - 1) + pasWidth
        } else if (xy.y == room.bottom) {
            return (room.right - xy.x - 1) + pasWidth + pasHeight
        } else /*if (xy.x == room.left)*/ {
            if (xy.y == room.top + 1) {
                return 0
            } else {
                return (room.bottom - xy.y - 1) + pasWidth * 2 + pasHeight
            }
        }
    }
    private fun p2xy(room: Room, p: Int, pasWidth: Int, pasHeight: Int): Point {
        if (p < pasWidth) {
            return Point(room.left + 1 + p, room.top + 1)
        } else if (p < pasWidth + pasHeight) {
            return Point(room.right - 1, room.top + 1 + (p - pasWidth))
        } else if (p < pasWidth * 2 + pasHeight) {
            return Point(room.right - 1 - (p - (pasWidth + pasHeight)), room.bottom - 1)
        } else {
            return Point(room.left + 1, room.bottom - 1 - (p - (pasWidth * 2 + pasHeight)))
        }
    }
}
