package com.watabou.pixeldungeon.levels.painters

import com.watabou.pixeldungeon.items.crafting.ArcaneDust
import com.watabou.pixeldungeon.items.crafting.BlankTome
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random

object EnchantingTablePainter : Painter() {

    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)

        val entrance = room.entrance() ?: return

        // Place enchanting table in center
        val center = room.center()
        Painter.set(level, center, Terrain.ENCHANTING_TABLE)

        // Drop 1-2 ArcaneDust
        val n = Random.IntRange(1, 2)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != Terrain.EMPTY_SP || level.heaps[pos] != null)
            level.drop(ArcaneDust().apply { quantity = Random.IntRange(1, 3) }, pos)
        }

        // 30% chance for BlankTome
        if (Random.Float() < 0.3f) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != Terrain.EMPTY_SP || level.heaps[pos] != null)
            level.drop(BlankTome(), pos)
        }

        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
}
