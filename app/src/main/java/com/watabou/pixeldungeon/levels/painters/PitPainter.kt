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
import com.watabou.utils.Random
object PitPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val entrance = room.entrance() ?: return
        entrance.set(Room.Door.Type.LOCKED)
        var well: Point? = null
        if (entrance.x == room.left) {
            well = Point(room.right - 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.x == room.right) {
            well = Point(room.left + 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.y == room.top) {
            well = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.bottom - 1)
        } else if (entrance.y == room.bottom) {
            well = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.top + 1)
        }
        val wellPos = well ?: return
        Painter.set(level, wellPos, Terrain.EMPTY_WELL)
        var remains = room.random()
        while (level.map[remains] == Terrain.EMPTY_WELL) {
            remains = room.random()
        }
        level.drop(IronKey(), remains).type = Heap.Type.SKELETON
        if (Random.Int(5) == 0) {
            Generator.random(Generator.Category.RING)?.let { level.drop(it, remains) }
        } else {
            Generator.random(
                Random.oneOf(
                    Generator.Category.WEAPON,
                    Generator.Category.ARMOR
                )
            )?.let { level.drop(it, remains) }
        }
        val n = Random.IntRange(1, 2)
        for (i in 0 until n) {
            level.drop(prize(level), remains)
        }
    }
    private fun prize(level: Level): Item {
        val prize = level.itemToSpanAsPrize()
        if (prize != null) {
            return prize
        }
        return Generator.random(
            Random.oneOf(
                Generator.Category.POTION,
                Generator.Category.SCROLL,
                Generator.Category.FOOD,
                Generator.Category.GOLD
            )
        ) ?: Gold()
    }
}
