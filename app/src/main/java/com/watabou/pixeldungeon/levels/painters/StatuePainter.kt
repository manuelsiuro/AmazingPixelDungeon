package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Statue
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
object StatuePainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val c = room.center()
        var cx = c.x
        var cy = c.y
        val door = room.entrance() ?: return
        door.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
        if (door.x == room.left) {
            Painter.fill(level, room.right - 1, room.top + 1, 1, room.height() - 1, Terrain.STATUE)
            cx = room.right - 2
        } else if (door.x == room.right) {
            Painter.fill(level, room.left + 1, room.top + 1, 1, room.height() - 1, Terrain.STATUE)
            cx = room.left + 2
        } else if (door.y == room.top) {
            Painter.fill(level, room.left + 1, room.bottom - 1, room.width() - 1, 1, Terrain.STATUE)
            cy = room.bottom - 2
        } else if (door.y == room.bottom) {
            Painter.fill(level, room.left + 1, room.top + 1, room.width() - 1, 1, Terrain.STATUE)
            cy = room.top + 2
        }
        val statue = Statue()
        statue.pos = cx + cy * Level.WIDTH
        level.mobs.add(statue)
        Actor.occupyCell(statue)
    }
}
