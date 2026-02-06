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
        // Tier 1: Dramatic full-room decorations (20% chance)
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
        // Shape selection: non-rectangular shapes for visual variety
        val w = room.width()
        val h = room.height()
        val isLargeEnough = min(w, h) >= 7
        val isMedium = min(w, h) >= 5

        val shape = if (!Dungeon.bossLevel() && isMedium) {
            if (isLargeEnough) {
                // Full shape palette for 7x7+ rooms
                val idx = Random.chances(floatArrayOf(40f, 20f, 15f, 12f, 13f))
                idx
            } else {
                // Only rectangle or rounded rect for 5x5-6x6 rooms
                val idx = Random.chances(floatArrayOf(65f, 35f))
                idx
            }
        } else {
            SHAPE_RECTANGLE
        }

        when (shape) {
            SHAPE_RECTANGLE -> Painter.fill(level, room, 1, Terrain.EMPTY)
            SHAPE_ROUNDED_RECT -> carveRoundedRect(level, room)
            SHAPE_CIRCLE -> carveCircle(level, room)
            SHAPE_DIAMOND -> carveDiamond(level, room)
            SHAPE_CROSS -> carveCross(level, room)
            else -> Painter.fill(level, room, 1, Terrain.EMPTY)
        }

        // Ensure all doors can reach the interior floor
        if (shape != SHAPE_RECTANGLE) {
            ensureDoorConnectivity(level, room)
        }

        // Tier 2: Lightweight interior features (~50% of remaining rectangular rooms)
        if (shape == SHAPE_RECTANGLE && !Dungeon.bossLevel() && Random.Int(2) == 0) {
            when (Random.Int(5)) {
                0 -> paintPillars(level, room)
                1 -> paintCornerAlcoves(level, room)
                2 -> paintCenterFeature(level, room)
                3 -> paintWallNiches(level, room)
                4 -> paintScattered(level, room)
            }
        }
    }

    private fun isAdjacentToDoor(room: Room, x: Int, y: Int): Boolean {
        for (door in room.connected.values) {
            val d = door ?: continue
            if (abs(d.x - x) <= 1 && abs(d.y - y) <= 1) return true
        }
        return false
    }

    private fun paintPillars(level: Level, room: Room) {
        if (room.width() < 5 || room.height() < 5) return

        var x = room.left + 2
        while (x < room.right) {
            var y = room.top + 2
            while (y < room.bottom) {
                if (!isAdjacentToDoor(room, x, y)) {
                    Painter.set(level, x, y, Terrain.WALL)
                }
                y += 2
            }
            x += 2
        }
    }

    private fun paintCornerAlcoves(level: Level, room: Room) {
        if (room.width() < 5 || room.height() < 5) return

        val corners = arrayOf(
            Point(room.left + 1, room.top + 1),
            Point(room.right - 1, room.top + 1),
            Point(room.left + 1, room.bottom - 1),
            Point(room.right - 1, room.bottom - 1)
        )
        for (corner in corners) {
            if (Random.Float() < 0.4f && !isAdjacentToDoor(room, corner.x, corner.y)) {
                Painter.set(level, corner, Terrain.WALL)
            }
        }
    }

    private fun paintCenterFeature(level: Level, room: Room) {
        if (room.width() < 5 || room.height() < 5) return

        val c = room.center()

        if (room.width() >= 7 && room.height() >= 7 && Random.Int(2) == 0) {
            // 3x3 center feature
            val terrain = if (Random.Int(2) == 0) Terrain.WATER else Terrain.EMBERS
            Painter.fill(level, c.x - 1, c.y - 1, 3, 3, terrain)
        } else {
            // Single tile center feature
            val terrain = when (Random.Int(3)) {
                0 -> Terrain.PEDESTAL
                1 -> Terrain.STATUE
                else -> Terrain.EMPTY_SP
            }
            if (terrain == Terrain.STATUE && isAdjacentToDoor(room, c.x, c.y)) {
                Painter.set(level, c, Terrain.EMPTY_SP)
            } else {
                Painter.set(level, c, terrain)
            }
        }
    }

    private fun paintWallNiches(level: Level, room: Room) {
        if (room.width() < 5 || room.height() < 5) return

        for (x in room.left + 1 until room.right) {
            for (y in room.top + 1 until room.bottom) {
                // Only cells in the ring adjacent to walls (1 cell from border)
                val innerRing = (x == room.left + 1 || x == room.right - 1 ||
                        y == room.top + 1 || y == room.bottom - 1)
                if (!innerRing) continue
                // Skip corners (already handled by other decorators)
                val isCorner = (x == room.left + 1 || x == room.right - 1) &&
                        (y == room.top + 1 || y == room.bottom - 1)
                if (isCorner) continue

                if (Random.Float() < 0.25f) {
                    Painter.set(level, x, y, Terrain.EMPTY_SP)
                }
            }
        }
    }

    private fun paintScattered(level: Level, room: Room) {
        val regionTerrain = when {
            Dungeon.depth <= 5 -> Terrain.GRASS
            Dungeon.depth <= 10 -> Terrain.EMPTY_DECO
            Dungeon.depth <= 15 -> Terrain.EMBERS
            Dungeon.depth <= 20 -> Terrain.EMPTY_DECO
            else -> Terrain.EMBERS
        }

        for (x in room.left + 1 until room.right) {
            for (y in room.top + 1 until room.bottom) {
                if (Random.Float() < 0.08f) {
                    Painter.set(level, x, y, regionTerrain)
                }
            }
        }
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

    // --- Room Shape Variants ---

    private const val SHAPE_RECTANGLE = 0
    private const val SHAPE_ROUNDED_RECT = 1
    private const val SHAPE_CIRCLE = 2
    private const val SHAPE_DIAMOND = 3
    private const val SHAPE_CROSS = 4

    private fun carveRoundedRect(level: Level, room: Room) {
        // Start with full rectangle carved
        Painter.fill(level, room, 1, Terrain.EMPTY)

        val w = room.width()
        val h = room.height()

        if (w >= 7 && h >= 7) {
            // Large rooms: clip L-shaped 3-cell chunks from each corner (octagonal look)
            // Top-left corner
            Painter.set(level, room.left + 1, room.top + 1, Terrain.WALL)
            Painter.set(level, room.left + 2, room.top + 1, Terrain.WALL)
            Painter.set(level, room.left + 1, room.top + 2, Terrain.WALL)
            // Top-right corner
            Painter.set(level, room.right - 1, room.top + 1, Terrain.WALL)
            Painter.set(level, room.right - 2, room.top + 1, Terrain.WALL)
            Painter.set(level, room.right - 1, room.top + 2, Terrain.WALL)
            // Bottom-left corner
            Painter.set(level, room.left + 1, room.bottom - 1, Terrain.WALL)
            Painter.set(level, room.left + 2, room.bottom - 1, Terrain.WALL)
            Painter.set(level, room.left + 1, room.bottom - 2, Terrain.WALL)
            // Bottom-right corner
            Painter.set(level, room.right - 1, room.bottom - 1, Terrain.WALL)
            Painter.set(level, room.right - 2, room.bottom - 1, Terrain.WALL)
            Painter.set(level, room.right - 1, room.bottom - 2, Terrain.WALL)
        } else {
            // Smaller rooms (5x5, 6x6): just clip single corner cells
            Painter.set(level, room.left + 1, room.top + 1, Terrain.WALL)
            Painter.set(level, room.right - 1, room.top + 1, Terrain.WALL)
            Painter.set(level, room.left + 1, room.bottom - 1, Terrain.WALL)
            Painter.set(level, room.right - 1, room.bottom - 1, Terrain.WALL)
        }
    }

    private fun carveCircle(level: Level, room: Room) {
        val cx = (room.left + room.right) / 2.0f
        val cy = (room.top + room.bottom) / 2.0f
        val rx = (room.right - room.left - 2) / 2.0f
        val ry = (room.bottom - room.top - 2) / 2.0f

        for (x in room.left + 1 until room.right) {
            for (y in room.top + 1 until room.bottom) {
                val dx = (x - cx) / rx
                val dy = (y - cy) / ry
                if (dx * dx + dy * dy <= 1.0f) {
                    Painter.set(level, x, y, Terrain.EMPTY)
                }
            }
        }
    }

    private fun carveDiamond(level: Level, room: Room) {
        val cx = (room.left + room.right) / 2.0f
        val cy = (room.top + room.bottom) / 2.0f
        val hw = (room.right - room.left - 2) / 2.0f
        val hh = (room.bottom - room.top - 2) / 2.0f

        for (x in room.left + 1 until room.right) {
            for (y in room.top + 1 until room.bottom) {
                val dx = abs(x - cx) / hw
                val dy = abs(y - cy) / hh
                if (dx + dy <= 1.0f) {
                    Painter.set(level, x, y, Terrain.EMPTY)
                }
            }
        }
    }

    private fun carveCross(level: Level, room: Room) {
        val cx = (room.left + room.right) / 2
        val cy = (room.top + room.bottom) / 2

        // Horizontal bar: 3 cells tall centered vertically, full interior width
        for (x in room.left + 1 until room.right) {
            for (dy in -1..1) {
                val y = cy + dy
                if (y > room.top && y < room.bottom) {
                    Painter.set(level, x, y, Terrain.EMPTY)
                }
            }
        }
        // Vertical bar: 3 cells wide centered horizontally, full interior height
        for (y in room.top + 1 until room.bottom) {
            for (dx in -1..1) {
                val x = cx + dx
                if (x > room.left && x < room.right) {
                    Painter.set(level, x, y, Terrain.EMPTY)
                }
            }
        }
    }

    private fun ensureDoorConnectivity(level: Level, room: Room) {
        for (door in room.connected.values) {
            val d = door ?: continue
            // Determine inward direction from the door
            val dx: Int
            val dy: Int
            when {
                d.x == room.left -> { dx = 1; dy = 0 }
                d.x == room.right -> { dx = -1; dy = 0 }
                d.y == room.top -> { dx = 0; dy = 1 }
                d.y == room.bottom -> { dx = 0; dy = -1 }
                else -> continue
            }
            // Walk inward from door until we hit existing floor
            var x = d.x + dx
            var y = d.y + dy
            while (x > room.left && x < room.right && y > room.top && y < room.bottom) {
                val cell = y * Level.WIDTH + x
                if (level.map[cell] == Terrain.EMPTY) break
                level.map[cell] = Terrain.EMPTY
                x += dx
                y += dy
            }
        }
    }
}
