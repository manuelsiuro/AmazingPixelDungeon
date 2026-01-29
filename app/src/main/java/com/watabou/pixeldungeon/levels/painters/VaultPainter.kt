package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.GoldenKey
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object VaultPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)
        Painter.fill(level, room, 2, Terrain.EMPTY)
        val cx = (room.left + room.right) / 2
        val cy = (room.top + room.bottom) / 2
        val c = cx + cy * Level.WIDTH
        when (Random.Int(3)) {
            0 -> {
                level.drop(prize(level), c).type = Heap.Type.LOCKED_CHEST
                level.addItemToSpawn(GoldenKey())
            }
            1 -> {
                var i1: Item
                var i2: Item
                do {
                    i1 = prize(level)
                    i2 = prize(level)
                } while (i1.javaClass == i2.javaClass)
                level.drop(i1, c).type = Heap.Type.CRYSTAL_CHEST
                level.drop(i2, c + Level.NEIGHBOURS8[Random.Int(8)]).type = Heap.Type.CRYSTAL_CHEST
                level.addItemToSpawn(GoldenKey())
            }
            2 -> {
                level.drop(prize(level), c)
                Painter.set(level, c, Terrain.PEDESTAL)
            }
        }
        room.entrance()?.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
    @Suppress("UNUSED_PARAMETER")
    private fun prize(level: Level): Item {
        return Generator.random(
            Random.oneOf(
                Generator.Category.WAND,
                Generator.Category.RING
            )
        ) ?: Gold()
    }
}
