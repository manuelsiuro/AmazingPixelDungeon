package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Point
import com.watabou.utils.Random
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
object StandardPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        Painter.fill(level, room, Terrain.WALL)
        for (door in room.connected.values) {
            door?.set(Room.Door.Type.REGULAR)
        }
        if (!Dungeon.bossLevel() && Random.Int(5) == 0) {
            when (Random.Int(6)) {
                0 -> if (level.feeling !== Level.Feeling.GRASS) {
                    if (min(room.width(), room.height()) >= 4 && max(
                            room.width(),
                            room.height()
                        ) >= 6
                    ) {
                        paintGraveyard(level, room)
                        return
                    }
                } else {
                    // Burned room
                }
                1 -> {
                    if (Dungeon.depth > 1) {
                        paintBurned(level, room)
                        return
                    }
                }
                2 -> if (max(room.width(), room.height()) >= 4) {
                    paintStriped(level, room)
                    return
                }
                3 -> if (room.width() >= 6 && room.height() >= 6) {
                    paintStudy(level, room)
                    return
                }
                4 -> if (level.feeling !== Level.Feeling.WATER) {
                    if (room.connected.size == 2 && room.width() >= 4 && room.height() >= 4) {
                        paintBridge(level, room)
                        return
                    }
                } else {
                    // Fissure
                }
                5 -> if (!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1) &&
                    min(room.width(), room.height()) >= 5
                ) {
                    paintFissure(level, room)
                    return
                }
            }
        }
        Painter.fill(level, room, 1, Terrain.EMPTY)
    }
    private fun paintBurned(level: Level, room: Room) {
        for (i in room.top + 1 until room.bottom) {
            for (j in room.left + 1 until room.right) {
                var t = Terrain.EMBERS
                when (Random.Int(5)) {
                    0 -> t = Terrain.EMPTY
                    1 -> t = Terrain.FIRE_TRAP
                    2 -> t = Terrain.SECRET_FIRE_TRAP
                    3 -> t = Terrain.INACTIVE_TRAP
                }
                level.map[i * Level.WIDTH + j] = t
            }
        }
    }
    private fun paintGraveyard(level: Level, room: Room) {
        Painter.fill(
            level,
            room.left + 1,
            room.top + 1,
            room.width() - 1,
            room.height() - 1,
            Terrain.GRASS
        )
        val w = room.width() - 1
        val h = room.height() - 1
        val nGraves = max(w, h) / 2
        val index = Random.Int(nGraves)
        val shift = Random.Int(2)
        for (i in 0 until nGraves) {
            val pos = if (w > h)
                room.left + 1 + shift + i * 2 + (room.top + 2 + Random.Int(h - 2)) * Level.WIDTH
            else
                (room.left + 2 + Random.Int(w - 2)) + (room.top + 1 + shift + i * 2) * Level.WIDTH
            level.drop(if (i == index) (Generator.random() ?: Gold()) else Gold(), pos).type = Heap.Type.TOMB
        }
    }
    private fun paintStriped(level: Level, room: Room) {
        Painter.fill(
            level,
            room.left + 1,
            room.top + 1,
            room.width() - 1,
            room.height() - 1,
            Terrain.EMPTY_SP
        )
        if (room.width() > room.height()) {
            var i = room.left + 2
            while (i < room.right) {
                Painter.fill(level, i, room.top + 1, 1, room.height() - 1, Terrain.HIGH_GRASS)
                i += 2
            }
        } else {
            var i = room.top + 2
            while (i < room.bottom) {
                Painter.fill(level, room.left + 1, i, room.width() - 1, 1, Terrain.HIGH_GRASS)
                i += 2
            }
        }
    }
    private fun paintStudy(level: Level, room: Room) {
        Painter.fill(
            level,
            room.left + 1,
            room.top + 1,
            room.width() - 1,
            room.height() - 1,
            Terrain.BOOKSHELF
        )
        Painter.fill(
            level,
            room.left + 2,
            room.top + 2,
            room.width() - 3,
            room.height() - 3,
            Terrain.EMPTY_SP
        )
        for (door in room.connected.values) {
            val doorPos = door ?: continue
            if (doorPos.x == room.left) {
                Painter.set(level, doorPos.x + 1, doorPos.y, Terrain.EMPTY)
            } else if (doorPos.x == room.right) {
                Painter.set(level, doorPos.x - 1, doorPos.y, Terrain.EMPTY)
            } else if (doorPos.y == room.top) {
                Painter.set(level, doorPos.x, doorPos.y + 1, Terrain.EMPTY)
            } else if (doorPos.y == room.bottom) {
                Painter.set(level, doorPos.x, doorPos.y - 1, Terrain.EMPTY)
            }
        }
        Painter.set(level, room.center(), Terrain.PEDESTAL)
    }
    private fun paintBridge(level: Level, room: Room) {
        Painter.fill(
            level, room.left + 1, room.top + 1, room.width() - 1, room.height() - 1,
            if (!Dungeon.bossLevel() && !Dungeon.bossLevel(Dungeon.depth + 1) && Random.Int(3) == 0)
                Terrain.CHASM
            else
                Terrain.WATER
        )
        var door1: Point? = null
        var door2: Point? = null
        for (p in room.connected.values) {
            if (door1 == null) {
                door1 = p
            } else {
                door2 = p
            }
        }
        val d1 = door1 ?: return
        val d2 = door2 ?: return
        if (d1.x == room.left && d2.x == room.right || d1.x == room.right && d2.x == room.left) {
            val s = room.width() / 2
            Painter.drawInside(level, room, d1, s, Terrain.EMPTY_SP)
            Painter.drawInside(level, room, d2, s, Terrain.EMPTY_SP)
            Painter.fill(
                level,
                room.center().x,
                min(d1.y, d2.y),
                1,
                abs(d1.y - d2.y) + 1,
                Terrain.EMPTY_SP
            )
        } else if (d1.y == room.top && d2.y == room.bottom || d1.y == room.bottom && d2.y == room.top) {
            val s = room.height() / 2
            Painter.drawInside(level, room, d1, s, Terrain.EMPTY_SP)
            Painter.drawInside(level, room, d2, s, Terrain.EMPTY_SP)
            Painter.fill(
                level,
                min(d1.x, d2.x),
                room.center().y,
                abs(d1.x - d2.x) + 1,
                1,
                Terrain.EMPTY_SP
            )
        } else if (d1.x == d2.x) {
            Painter.fill(
                level,
                if (d1.x == room.left) room.left + 1 else room.right - 1,
                min(d1.y, d2.y),
                1,
                abs(d1.y - d2.y) + 1,
                Terrain.EMPTY_SP
            )
        } else if (d1.y == d2.y) {
            Painter.fill(
                level,
                min(d1.x, d2.x),
                if (d1.y == room.top) room.top + 1 else room.bottom - 1,
                abs(d1.x - d2.x) + 1,
                1,
                Terrain.EMPTY_SP
            )
        } else if (d1.y == room.top || d1.y == room.bottom) {
            Painter.drawInside(level, room, d1, abs(d1.y - d2.y), Terrain.EMPTY_SP)
            Painter.drawInside(level, room, d2, abs(d1.x - d2.x), Terrain.EMPTY_SP)
        } else if (d1.x == room.left || d1.x == room.right) {
            Painter.drawInside(level, room, d1, abs(d1.x - d2.x), Terrain.EMPTY_SP)
            Painter.drawInside(level, room, d2, abs(d1.y - d2.y), Terrain.EMPTY_SP)
        }
    }
    private fun paintFissure(level: Level, room: Room) {
        Painter.fill(
            level,
            room.left + 1,
            room.top + 1,
            room.width() - 1,
            room.height() - 1,
            Terrain.EMPTY
        )
        for (i in room.top + 2 until room.bottom - 1) {
            for (j in room.left + 2 until room.right - 1) {
                val v = min(i - room.top, room.bottom - i)
                val h = min(j - room.left, room.right - j)
                if (min(v, h) > 2 || Random.Int(2) == 0) {
                    Painter.set(level, j, i, Terrain.CHASM)
                }
            }
        }
    }
}
