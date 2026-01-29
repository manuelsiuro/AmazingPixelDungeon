package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.blobs.Alchemy
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
object LaboratoryPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)
        val entrance = room.entrance() ?: return
        var pot: Point? = null
        if (entrance.x == room.left) {
            pot = Point(room.right - 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.x == room.right) {
            pot = Point(room.left + 1, if (Random.Int(2) == 0) room.top + 1 else room.bottom - 1)
        } else if (entrance.y == room.top) {
            pot = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.bottom - 1)
        } else if (entrance.y == room.bottom) {
            pot = Point(if (Random.Int(2) == 0) room.left + 1 else room.right - 1, room.top + 1)
        }
        if (pot != null) {
            Painter.set(level, pot, Terrain.ALCHEMY)
            val alchemy = Alchemy()
            alchemy.seed(pot.x + Level.WIDTH * pot.y, 1)
            level.blobs[Alchemy::class.java] = alchemy
        }
        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (
                level.map[pos] != Terrain.EMPTY_SP ||
                level.heaps[pos] != null
            )
            level.drop(prize(level), pos)
        }
        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
    private fun prize(level: Level): Item {
        val prize = level.itemToSpanAsPrize()
        if (prize is Potion) {
            return prize
        } else if (prize != null) {
            level.addItemToSpawn(prize)
        }
        return Generator.random(Generator.Category.POTION) ?: PotionOfHealing()
    }
}
