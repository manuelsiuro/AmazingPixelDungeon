package com.watabou.pixeldungeon.levels.painters

import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.crafting.WoodPlank
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random

object WorkshopPainter : Painter() {

    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)

        val entrance = room.entrance() ?: return

        // Place crafting table and furnace in corners opposite the entrance
        var table: Point? = null
        var furnace: Point? = null

        if (entrance.x == room.left) {
            table = Point(room.right - 1, room.top + 1)
            furnace = Point(room.right - 1, room.bottom - 1)
        } else if (entrance.x == room.right) {
            table = Point(room.left + 1, room.top + 1)
            furnace = Point(room.left + 1, room.bottom - 1)
        } else if (entrance.y == room.top) {
            table = Point(room.left + 1, room.bottom - 1)
            furnace = Point(room.right - 1, room.bottom - 1)
        } else if (entrance.y == room.bottom) {
            table = Point(room.left + 1, room.top + 1)
            furnace = Point(room.right - 1, room.top + 1)
        }

        if (table != null) {
            Painter.set(level, table, Terrain.CRAFTING_TABLE)
        }
        if (furnace != null) {
            Painter.set(level, furnace, Terrain.FURNACE)
        }

        // Drop 2-3 random materials as prizes
        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (
                level.map[pos] != Terrain.EMPTY_SP ||
                level.heaps[pos] != null
            )
            level.drop(prize(), pos)
        }

        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }

    private fun prize(): Item {
        return when (Random.Int(4)) {
            0 -> Cobblestone().apply { quantity = Random.IntRange(2, 4) }
            1 -> IronOre().apply { quantity = Random.IntRange(1, 2) }
            2 -> Stick().apply { quantity = Random.IntRange(2, 4) }
            else -> WoodPlank().apply { quantity = Random.IntRange(1, 3) }
        }
    }
}
