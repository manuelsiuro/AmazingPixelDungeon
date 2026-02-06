package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
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

        // Wider path segments: widen some perimeter path cells inward
        if (pasWidth >= 3 && pasHeight >= 3 && Random.Int(3) == 0) {
            for (x in room.left + 1 until room.right) {
                for (y in room.top + 1 until room.bottom) {
                    val cell = y * Level.WIDTH + x
                    if (level.map[cell] != floor) continue
                    // Only widen perimeter ring cells (1 cell from wall)
                    val onPerimeter = (x == room.left + 1 || x == room.right - 1 ||
                            y == room.top + 1 || y == room.bottom - 1)
                    if (!onPerimeter) continue
                    if (Random.Float() > 0.35f) continue
                    // Carve 1 cell inward from this perimeter cell
                    val inX = if (x == room.left + 1) x + 1
                             else if (x == room.right - 1) x - 1
                             else x
                    val inY = if (y == room.top + 1) y + 1
                             else if (y == room.bottom - 1) y - 1
                             else y
                    if (inX > room.left + 1 && inX < room.right - 1 &&
                        inY > room.top + 1 && inY < room.bottom - 1
                    ) {
                        val inCell = inY * Level.WIDTH + inX
                        if (level.map[inCell] == Terrain.WALL) {
                            level.map[inCell] = floor
                        }
                    }
                }
            }
        }

        // Courtyard fill: fill interior with grass or water
        if (pasWidth >= 3 && pasHeight >= 3 && Random.Int(4) == 0) {
            val fillTerrain = if (Random.Int(2) == 0) Terrain.GRASS else Terrain.WATER
            for (x in room.left + 2 until room.right - 1) {
                for (y in room.top + 2 until room.bottom - 1) {
                    val cell = y * Level.WIDTH + x
                    if (level.map[cell] == Terrain.WALL) {
                        level.map[cell] = fillTerrain
                    }
                }
            }

            // Courtyard center feature
            if (pasWidth >= 5 && pasHeight >= 5 && Random.Int(3) == 0) {
                val cx = (room.left + room.right) / 2
                val cy = (room.top + room.bottom) / 2
                val centerTerrain = when (Random.Int(3)) {
                    0 -> Terrain.PEDESTAL
                    1 -> Terrain.STATUE
                    else -> Terrain.EMPTY_DECO
                }
                Painter.set(level, cx, cy, centerTerrain)
            }
        }

        // Corner features along the perimeter path
        val cornerPs = intArrayOf(0, pasWidth, pasWidth + pasHeight, pasWidth * 2 + pasHeight)
        for (cp in cornerPs) {
            if (Random.Int(5) == 0) {
                val pt = p2xy(room, cp % perimeter, pasWidth, pasHeight)
                val cell = pt.y * Level.WIDTH + pt.x
                if (level.map[cell] == floor) {
                    level.map[cell] = Terrain.EMPTY_DECO
                }
            }
        }

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
