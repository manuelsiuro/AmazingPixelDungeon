package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
object EntrancePainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.REGULAR)
        }
        level.entrance = room.random(1)
        Painter.set(level, level.entrance, Terrain.ENTRANCE)
    }
}
