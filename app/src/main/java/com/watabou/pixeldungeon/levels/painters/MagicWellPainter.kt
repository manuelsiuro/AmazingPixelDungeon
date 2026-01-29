package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.blobs.WaterOfAwareness
import com.watabou.pixeldungeon.actors.blobs.WaterOfHealth
import com.watabou.pixeldungeon.actors.blobs.WaterOfTransmutation
import com.watabou.pixeldungeon.actors.blobs.WellWater
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object MagicWellPainter : Painter() {
    private val WATERS = arrayOf(
        WaterOfAwareness::class.java,
        WaterOfHealth::class.java,
        WaterOfTransmutation::class.java
    )
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY)
        val c = room.center()
        Painter.set(level, c.x, c.y, Terrain.WELL)
        val waterClass = Random.element(WATERS)
        var water = level.blobs[waterClass] as WellWater?
        if (water == null) {
            try {
                water = waterClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                water = null
            }
        }
        water?.seed(c.x + Level.WIDTH * c.y, 1)
        water?.let { level.blobs[waterClass] = it }
        room.entrance()?.set(Room.Door.Type.REGULAR)
    }
}
