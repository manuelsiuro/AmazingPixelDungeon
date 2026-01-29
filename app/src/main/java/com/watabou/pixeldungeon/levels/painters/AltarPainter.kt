package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.SacrificialFire
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import kotlin.math.abs
object AltarPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(
            level,
            room,
            1,
            if (Dungeon.bossLevel(Dungeon.depth + 1)) Terrain.HIGH_GRASS else Terrain.CHASM
        )
        val c = room.center()
        val door = room.entrance() ?: return
        if (door.x == room.left || door.x == room.right) {
            var p = Painter.drawInside(level, room, door, abs(door.x - c.x) - 2, Terrain.EMPTY_SP)
            while (p.y != c.y) {
                Painter.set(level, p, Terrain.EMPTY_SP)
                p.y += if (p.y < c.y) +1 else -1
            }
        } else {
            var p = Painter.drawInside(level, room, door, abs(door.y - c.y) - 2, Terrain.EMPTY_SP)
            while (p.x != c.x) {
                Painter.set(level, p, Terrain.EMPTY_SP)
                p.x += if (p.x < c.x) +1 else -1
            }
        }
        Painter.fill(level, c.x - 1, c.y - 1, 3, 3, Terrain.EMBERS)
        Painter.set(level, c, Terrain.PEDESTAL)
        var fire = level.blobs[SacrificialFire::class.java] as SacrificialFire?
        if (fire == null) {
            fire = SacrificialFire()
        }
        fire.seed(c.x + c.y * Level.WIDTH, 5 + Dungeon.depth * 5)
        level.blobs[SacrificialFire::class.java] = fire
        door.set(Room.Door.Type.EMPTY)
    }
}
