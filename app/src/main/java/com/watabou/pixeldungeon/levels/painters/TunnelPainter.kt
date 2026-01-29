package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.utils.Random
object TunnelPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        val floor = level.tunnelTile()
        val c = room.center()
        if (room.width() > room.height() || (room.width() == room.height() && Random.Int(2) == 0)) {
            var from = room.right - 1
            var to = room.left + 1
            for (door in room.connected.values) {
                val doorPos = door ?: continue
                val step = if (doorPos.y < c.y) +1 else -1
                if (doorPos.x == room.left) {
                    from = room.left + 1
                    var i = doorPos.y
                    while (i != c.y) {
                        Painter.set(level, from, i, floor)
                        i += step
                    }
                } else if (doorPos.x == room.right) {
                    to = room.right - 1
                    var i = doorPos.y
                    while (i != c.y) {
                        Painter.set(level, to, i, floor)
                        i += step
                    }
                } else {
                    if (doorPos.x < from) {
                        from = doorPos.x
                    }
                    if (doorPos.x > to) {
                        to = doorPos.x
                    }
                    var i = doorPos.y + step
                    while (i != c.y) {
                        Painter.set(level, doorPos.x, i, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                Painter.set(level, i, c.y, floor)
            }
        } else {
            var from = room.bottom - 1
            var to = room.top + 1
            for (door in room.connected.values) {
                val doorPos = door ?: continue
                val step = if (doorPos.x < c.x) +1 else -1
                if (doorPos.y == room.top) {
                    from = room.top + 1
                    var i = doorPos.x
                    while (i != c.x) {
                        Painter.set(level, i, from, floor)
                        i += step
                    }
                } else if (doorPos.y == room.bottom) {
                    to = room.bottom - 1
                    var i = doorPos.x
                    while (i != c.x) {
                        Painter.set(level, i, to, floor)
                        i += step
                    }
                } else {
                    if (doorPos.y < from) {
                        from = doorPos.y
                    }
                    if (doorPos.y > to) {
                        to = doorPos.y
                    }
                    var i = doorPos.x + step
                    while (i != c.x) {
                        Painter.set(level, i, doorPos.y, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                Painter.set(level, c.x, i, floor)
            }
        }
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.TUNNEL)
        }
    }
}
