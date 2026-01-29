package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object StoragePainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        val floor = Terrain.EMPTY_SP
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, floor)
        val n = Random.IntRange(3, 4)
        for (i in 0 until n) {
            var pos: Int
            do {
                pos = room.random()
            } while (level.map[pos] != floor)
            level.drop(prize(level), pos)
        }
        room.entrance()?.set(Room.Door.Type.BARRICADE)
        level.addItemToSpawn(PotionOfLiquidFlame())
    }
    private fun prize(level: Level): Item {
        val prize = level.itemToSpanAsPrize()
        if (prize != null) {
            return prize
        }
        return Generator.random(
            Random.oneOf(
                Generator.Category.POTION,
                Generator.Category.SCROLL,
                Generator.Category.FOOD,
                Generator.Category.GOLD,
                Generator.Category.MISC
            )
        ) ?: Gold()
    }
}
