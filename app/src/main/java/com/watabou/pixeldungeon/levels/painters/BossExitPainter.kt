package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
object BossExitPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.REGULAR)
        }
        level.exit = room.top * Level.WIDTH + (room.left + room.right) / 2
        Painter.set(level, level.exit, Terrain.LOCKED_EXIT)
    }
}
