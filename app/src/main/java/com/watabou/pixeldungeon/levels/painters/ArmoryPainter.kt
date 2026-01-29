package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Bomb
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
object ArmoryPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val entrance = room.entrance() ?: return
        var statue: Point? = null
        if (entrance.x == room.left) {
            statue = Point(room.right - 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.x == room.right) {
            statue = Point(room.left + 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.y == room.top) {
            statue = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.bottom - 1)
        } else if (entrance.y == room.bottom) {
            statue = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.top + 1)
        }
        if (statue != null) {
            Painter.set(level, statue, Terrain.STATUE)
        }
        val n = 3 + if (Random.Int(4) == 0) 1 else 0
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
    @Suppress("UNUSED_PARAMETER")
    private fun prize(level: Level): Item {
        return if (Random.Int(6) == 0)
            Bomb().random()
        else
            Generator.random(
                Random.oneOf(
                    Generator.Category.ARMOR,
                    Generator.Category.WEAPON
                )
            ) ?: Gold()
    }
}
