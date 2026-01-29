package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
object LibraryPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val entrance = room.entrance() ?: return
        var a: Point? = null
        var b: Point? = null
        if (entrance.x == room.left) {
            a = Point(room.left + 1, entrance.y - 1)
            b = Point(room.left + 1, entrance.y + 1)
            Painter.fill(level, room.right - 1, room.top + 1, 1, room.height() - 1, Terrain.BOOKSHELF)
        } else if (entrance.x == room.right) {
            a = Point(room.right - 1, entrance.y - 1)
            b = Point(room.right - 1, entrance.y + 1)
            Painter.fill(level, room.left + 1, room.top + 1, 1, room.height() - 1, Terrain.BOOKSHELF)
        } else if (entrance.y == room.top) {
            a = Point(entrance.x + 1, room.top + 1)
            b = Point(entrance.x - 1, room.top + 1)
            Painter.fill(level, room.left + 1, room.bottom - 1, room.width() - 1, 1, Terrain.BOOKSHELF)
        } else if (entrance.y == room.bottom) {
            a = Point(entrance.x + 1, room.bottom - 1)
            b = Point(entrance.x - 1, room.bottom - 1)
            Painter.fill(level, room.left + 1, room.top + 1, room.width() - 1, 1, Terrain.BOOKSHELF)
        }
        if (a != null && level.map[a.x + a.y * Level.WIDTH] == Terrain.EMPTY) {
            Painter.set(level, a, Terrain.STATUE)
        }
        if (b != null && level.map[b.x + b.y * Level.WIDTH] == Terrain.EMPTY) {
            Painter.set(level, b, Terrain.STATUE)
        }
        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != Terrain.EMPTY || level.heaps[pos] != null)
            level.drop(prize(level), pos)
        }
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
    private fun prize(level: Level): Item {
        val prize = level.itemToSpanAsPrize()
        if (prize is Scroll) {
            return prize
        } else if (prize != null) {
            level.addItemToSpawn(prize)
        }
        return Generator.random(Generator.Category.SCROLL) ?: ScrollOfIdentify()
    }
}
