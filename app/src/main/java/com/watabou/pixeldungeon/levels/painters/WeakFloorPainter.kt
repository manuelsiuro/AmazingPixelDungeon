package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
object WeakFloorPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.CHASM)
        val door = room.entrance() ?: return
        door.set(Room.Door.Type.REGULAR)
        if (door.x == room.left) {
            for (i in room.top + 1 until room.bottom) {
                Painter.drawInside(
                    level,
                    room,
                    Point(room.left, i),
                    Random.IntRange(1, room.width() - 2),
                    Terrain.EMPTY_SP
                )
            }
        } else if (door.x == room.right) {
            for (i in room.top + 1 until room.bottom) {
                Painter.drawInside(
                    level,
                    room,
                    Point(room.right, i),
                    Random.IntRange(1, room.width() - 2),
                    Terrain.EMPTY_SP
                )
            }
        } else if (door.y == room.top) {
            for (i in room.left + 1 until room.right) {
                Painter.drawInside(
                    level,
                    room,
                    Point(i, room.top),
                    Random.IntRange(1, room.height() - 2),
                    Terrain.EMPTY_SP
                )
            }
        } else if (door.y == room.bottom) {
            for (i in room.left + 1 until room.right) {
                Painter.drawInside(
                    level,
                    room,
                    Point(i, room.bottom),
                    Random.IntRange(1, room.height() - 2),
                    Terrain.EMPTY_SP
                )
            }
        }
    }
}
