package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object TreasuryPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        Painter.set(level, room.center(), Terrain.STATUE)
        val heapType = if (Random.Int(2) == 0) Heap.Type.CHEST else Heap.Type.HEAP
        val n = Random.IntRange(2, 3)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != Terrain.EMPTY || level.heaps[pos] != null)
            level.drop(Gold().random(), pos).type =
                if (i == 0 && heapType == Heap.Type.CHEST) Heap.Type.MIMIC else heapType
        }
        if (heapType == Heap.Type.HEAP) {
            for (i in 0 until 6) {
                var pos: Int
                do {
                    pos = room.random()
                } while (level.map[pos] != Terrain.EMPTY)
                level.drop(Gold(Random.IntRange(1, 3)), pos)
            }
        }
        room.entrance()?.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
}
