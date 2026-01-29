package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object BlacksmithPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.FIRE_TRAP)
        Painter.fill(level, room, 2, Terrain.EMPTY_SP)
        for (i in 0 until 2) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != Terrain.EMPTY_SP)
            Generator.random(
                Random.oneOf(
                    Generator.Category.ARMOR,
                    Generator.Category.WEAPON
                )
            )?.let { level.drop(it, pos) }
        }
        for (door in room.connected.values) {
            door?.let {
                it.set(Room.Door.Type.UNLOCKED)
                Painter.drawInside(level, room, it, 1, Terrain.EMPTY)
            }
        }
        val npc = Blacksmith()
        do {
            npc.pos = room.random(1)
        } while (level.heaps[npc.pos] != null)
        level.mobs.add(npc)
        Actor.occupyCell(npc)
    }
}
