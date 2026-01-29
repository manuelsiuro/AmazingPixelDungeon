package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.blobs.Foliage
import com.watabou.pixeldungeon.items.Honeypot
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.plants.Sungrass
import com.watabou.utils.Random
object GardenPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.HIGH_GRASS)
        Painter.fill(level, room, 2, Terrain.GRASS)
        room.entrance()?.set(Room.Door.Type.REGULAR)
        if (Random.Int(2) == 0) {
            level.drop(Honeypot(), room.random())
        } else {
            val bushes = if (Random.Int(5) == 0) 2 else 1
            for (i in 0 until bushes) {
                val pos = room.random()
                Painter.set(level, pos, Terrain.GRASS)
                level.plant(Sungrass.Seed(), pos)
            }
        }
        var light = level.blobs[Foliage::class.java] as Foliage?
        if (light == null) {
            light = Foliage()
        }
        for (i in room.top + 1 until room.bottom) {
            for (j in room.left + 1 until room.right) {
                light.seed(j + Level.WIDTH * i, 1)
            }
        }
        level.blobs[Foliage::class.java] = light
    }
}
