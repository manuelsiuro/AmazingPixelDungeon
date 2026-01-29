package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.PotionOfLevitation
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object TrapsPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        val traps = arrayOf(
            Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP, Terrain.TOXIC_TRAP,
            Terrain.PARALYTIC_TRAP, Terrain.PARALYTIC_TRAP,
            if (!Dungeon.bossLevel(Dungeon.depth + 1)) Terrain.CHASM else Terrain.SUMMONING_TRAP
        )
        Painter.fill(level, room, Terrain.WALL)
        val trapTile = Random.element(traps)
        Painter.fill(level, room, 1, trapTile)
        val door = room.entrance() ?: return
        door.set(Room.Door.Type.REGULAR)
        val lastRow =
            if (level.map[room.left + 1 + (room.top + 1) * Level.WIDTH] == Terrain.CHASM) Terrain.CHASM else Terrain.EMPTY
        var x = -1
        var y = -1
        if (door.x == room.left) {
            x = room.right - 1
            y = room.top + room.height() / 2
            Painter.fill(level, x, room.top + 1, 1, room.height() - 1, lastRow)
        } else if (door.x == room.right) {
            x = room.left + 1
            y = room.top + room.height() / 2
            Painter.fill(level, x, room.top + 1, 1, room.height() - 1, lastRow)
        } else if (door.y == room.top) {
            x = room.left + room.width() / 2
            y = room.bottom - 1
            Painter.fill(level, room.left + 1, y, room.width() - 1, 1, lastRow)
        } else if (door.y == room.bottom) {
            x = room.left + room.width() / 2
            y = room.top + 1
            Painter.fill(level, room.left + 1, y, room.width() - 1, 1, lastRow)
        }
        val pos = x + y * Level.WIDTH
        if (Random.Int(3) == 0) {
            if (lastRow == Terrain.CHASM) {
                Painter.set(level, pos, Terrain.EMPTY)
            }
            level.drop(prize(level), pos).type = Heap.Type.CHEST
        } else {
            Painter.set(level, pos, Terrain.PEDESTAL)
            level.drop(prize(level), pos)
        }
        level.addItemToSpawn(PotionOfLevitation())
    }
    private fun prize(level: Level): Item {
        val existing = level.itemToSpanAsPrize()
        if (existing != null) {
            return existing
        }
        var prize: Item = Generator.random(
            Random.oneOf(
                Generator.Category.WEAPON,
                Generator.Category.ARMOR
            )
        ) ?: return Gold()
        for (i in 0 until 3) {
            val another = Generator.random(
                Random.oneOf(
                    Generator.Category.WEAPON,
                    Generator.Category.ARMOR
                )
            ) ?: continue
            if (another.level() > prize.level()) {
                prize = another
            }
        }
        return prize
    }
}
