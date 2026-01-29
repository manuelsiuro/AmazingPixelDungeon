package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Piranha
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.PotionOfInvisibility
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object PoolPainter : Painter() {
    private const val NPIRANHAS = 3
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.WATER)
        val door = room.entrance() ?: return
        door.set(Room.Door.Type.REGULAR)
        var x = -1
        var y = -1
        if (door.x == room.left) {
            x = room.right - 1
            y = room.top + room.height() / 2
        } else if (door.x == room.right) {
            x = room.left + 1
            y = room.top + room.height() / 2
        } else if (door.y == room.top) {
            x = room.left + room.width() / 2
            y = room.bottom - 1
        } else if (door.y == room.bottom) {
            x = room.left + room.width() / 2
            y = room.top + 1
        }
        val pos = x + y * Level.WIDTH
        level.drop(prize(level), pos).type =
            if (Random.Int(3) == 0) Heap.Type.CHEST else Heap.Type.HEAP
        Painter.set(level, pos, Terrain.PEDESTAL)
        level.addItemToSpawn(PotionOfInvisibility())
        for (i in 0 until NPIRANHAS) {
            val piranha = Piranha()
            do {
                piranha.pos = room.random()
            } while (level.map[piranha.pos] != Terrain.WATER || Actor.findChar(piranha.pos) != null)
            level.mobs.add(piranha)
            Actor.occupyCell(piranha)
        }
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
        for (i in 0 until 4) {
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
