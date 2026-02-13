package com.watabou.pixeldungeon.levels.painters

import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain

object AnvilPainter : Painter() {

    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)

        val entrance = room.entrance() ?: return

        // Place anvil in center
        val center = room.center()
        Painter.set(level, center, Terrain.ANVIL)

        entrance.set(Room.Door.Type.LOCKED)
        level.addItemToSpawn(IronKey())
    }
}
