package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
object CryptPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val c = room.center()
        var cx = c.x
        var cy = c.y
        val entrance = room.entrance() ?: return
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
        if (entrance.x == room.left) {
            Painter.set(level, Point(room.right - 1, room.top + 1), Terrain.STATUE)
            Painter.set(level, Point(room.right - 1, room.bottom - 1), Terrain.STATUE)
            cx = room.right - 2
        } else if (entrance.x == room.right) {
            Painter.set(level, Point(room.left + 1, room.top + 1), Terrain.STATUE)
            Painter.set(level, Point(room.left + 1, room.bottom - 1), Terrain.STATUE)
            cx = room.left + 2
        } else if (entrance.y == room.top) {
            Painter.set(level, Point(room.left + 1, room.bottom - 1), Terrain.STATUE)
            Painter.set(level, Point(room.right - 1, room.bottom - 1), Terrain.STATUE)
            cy = room.bottom - 2
        } else if (entrance.y == room.bottom) {
            Painter.set(level, Point(room.left + 1, room.top + 1), Terrain.STATUE)
            Painter.set(level, Point(room.right - 1, room.top + 1), Terrain.STATUE)
            cy = room.top + 2
        }
        level.drop(prize(level), cx + cy * Level.WIDTH).type = Heap.Type.TOMB
    }
    @Suppress("UNUSED_PARAMETER")
    private fun prize(level: Level): Item {
        var prize = Generator.random(Generator.Category.ARMOR) ?: return Gold()
        for (i in 0 until 3) {
            val another = Generator.random(Generator.Category.ARMOR) ?: continue
            if (another.level() > prize.level()) {
                prize = another
            }
        }
        return prize
    }
}
