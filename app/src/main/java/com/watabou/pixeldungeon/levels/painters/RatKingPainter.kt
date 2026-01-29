package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.actors.mobs.npcs.RatKing
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object RatKingPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        Painter.fill(level, room, 1, Terrain.EMPTY_SP)
        val entrance = room.entrance() ?: return
        entrance.set(Room.Door.Type.HIDDEN)
        val door = entrance.x + entrance.y * Level.WIDTH
        for (i in room.left + 1 until room.right) {
            addChest(level, (room.top + 1) * Level.WIDTH + i, door)
            addChest(level, (room.bottom - 1) * Level.WIDTH + i, door)
        }
        for (i in room.top + 2 until room.bottom - 1) {
            addChest(level, i * Level.WIDTH + room.left + 1, door)
            addChest(level, i * Level.WIDTH + room.right - 1, door)
        }
        while (true) {
            val chest = level.heaps[room.random()]
            if (chest != null) {
                chest.type = Heap.Type.MIMIC
                break
            }
        }
        val king = RatKing()
        king.pos = room.random(1)
        level.mobs.add(king)
    }
    private fun addChest(level: Level, pos: Int, door: Int) {
        if (pos == door - 1 ||
            pos == door + 1 ||
            pos == door - Level.WIDTH ||
            pos == door + Level.WIDTH
        ) {
            return
        }
        val prize: Item
        when (Random.Int(10)) {
            0 -> {
                prize = Generator.random(Generator.Category.WEAPON) ?: return
                if (prize is MissileWeapon) {
                    prize.quantity(1)
                } else {
                    prize.degrade(Random.Int(3))
                }
            }
            1 -> prize = (Generator.random(Generator.Category.ARMOR) ?: return).degrade(Random.Int(3))
            else -> prize = Gold(Random.IntRange(1, 5))
        }
        level.drop(prize, pos).type = Heap.Type.CHEST
    }
}
