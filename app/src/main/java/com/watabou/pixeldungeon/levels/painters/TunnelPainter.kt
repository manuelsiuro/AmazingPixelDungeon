package com.watabou.pixeldungeon.levels.painters
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Room
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random
object TunnelPainter : Painter() {
    @JvmStatic
    fun paint(level: Level, room: Room) {
        val floor = level.tunnelTile()
        val c = room.center()
        if (room.width() > room.height() || (room.width() == room.height() && Random.Int(2) == 0)) {
            var from = room.right - 1
            var to = room.left + 1
            for (door in room.connected.values) {
                val doorPos = door ?: continue
                val step = if (doorPos.y < c.y) +1 else -1
                if (doorPos.x == room.left) {
                    from = room.left + 1
                    var i = doorPos.y
                    while (i != c.y) {
                        Painter.set(level, from, i, floor)
                        i += step
                    }
                } else if (doorPos.x == room.right) {
                    to = room.right - 1
                    var i = doorPos.y
                    while (i != c.y) {
                        Painter.set(level, to, i, floor)
                        i += step
                    }
                } else {
                    if (doorPos.x < from) {
                        from = doorPos.x
                    }
                    if (doorPos.x > to) {
                        to = doorPos.x
                    }
                    var i = doorPos.y + step
                    while (i != c.y) {
                        Painter.set(level, doorPos.x, i, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                Painter.set(level, i, c.y, floor)
            }

            // Widened tunnel: widen horizontal corridor vertically
            if (Random.Int(3) == 0 && room.height() >= 5) {
                val widenDir = if (Random.Int(2) == 0) 1 else -1
                for (i in from..to) {
                    val adjY = c.y + widenDir
                    if (adjY > room.top && adjY < room.bottom) {
                        val cell = adjY * Level.WIDTH + i
                        if (level.map[cell] == Terrain.WALL) {
                            level.map[cell] = floor
                        }
                    }
                }
                // Triple-wide: 50% chance to widen one more cell in same direction
                if (Random.Int(2) == 0 && room.height() >= 7) {
                    for (i in from..to) {
                        val adjY = c.y + widenDir * 2
                        if (adjY > room.top && adjY < room.bottom) {
                            val cell = adjY * Level.WIDTH + i
                            if (level.map[cell] == Terrain.WALL) {
                                level.map[cell] = floor
                            }
                        }
                    }
                }
            }
        } else {
            var from = room.bottom - 1
            var to = room.top + 1
            for (door in room.connected.values) {
                val doorPos = door ?: continue
                val step = if (doorPos.x < c.x) +1 else -1
                if (doorPos.y == room.top) {
                    from = room.top + 1
                    var i = doorPos.x
                    while (i != c.x) {
                        Painter.set(level, i, from, floor)
                        i += step
                    }
                } else if (doorPos.y == room.bottom) {
                    to = room.bottom - 1
                    var i = doorPos.x
                    while (i != c.x) {
                        Painter.set(level, i, to, floor)
                        i += step
                    }
                } else {
                    if (doorPos.y < from) {
                        from = doorPos.y
                    }
                    if (doorPos.y > to) {
                        to = doorPos.y
                    }
                    var i = doorPos.x + step
                    while (i != c.x) {
                        Painter.set(level, i, doorPos.y, floor)
                        i += step
                    }
                }
            }
            for (i in from..to) {
                Painter.set(level, c.x, i, floor)
            }

            // Widened tunnel: widen vertical corridor horizontally
            if (Random.Int(3) == 0 && room.width() >= 5) {
                val widenDir = if (Random.Int(2) == 0) 1 else -1
                for (i in from..to) {
                    val adjX = c.x + widenDir
                    if (adjX > room.left && adjX < room.right) {
                        val cell = i * Level.WIDTH + adjX
                        if (level.map[cell] == Terrain.WALL) {
                            level.map[cell] = floor
                        }
                    }
                }
                // Triple-wide: 50% chance to widen one more cell in same direction
                if (Random.Int(2) == 0 && room.width() >= 7) {
                    for (i in from..to) {
                        val adjX = c.x + widenDir * 2
                        if (adjX > room.left && adjX < room.right) {
                            val cell = i * Level.WIDTH + adjX
                            if (level.map[cell] == Terrain.WALL) {
                                level.map[cell] = floor
                            }
                        }
                    }
                }
            }
        }

        // Corridor alcoves: small niches perpendicular to corridor
        if (Random.Int(3) == 0) {
            val alcoveCount = Random.Int(1, 3)
            var placed = 0
            for (y in room.top + 1 until room.bottom) {
                for (x in room.left + 1 until room.right) {
                    if (placed >= alcoveCount) break
                    val cell = y * Level.WIDTH + x
                    if (level.map[cell] != floor) continue
                    // Try to place alcove perpendicular to this floor cell
                    val dirs = intArrayOf(-1, 1, -Level.WIDTH, Level.WIDTH)
                    for (dir in dirs) {
                        val adj = cell + dir
                        val adjX = adj % Level.WIDTH
                        val adjY = adj / Level.WIDTH
                        if (adjX > room.left && adjX < room.right &&
                            adjY > room.top && adjY < room.bottom &&
                            level.map[adj] == Terrain.WALL && Random.Int(8) == 0
                        ) {
                            level.map[adj] = floor
                            placed++
                            break
                        }
                    }
                }
                if (placed >= alcoveCount) break
            }
        }

        // Tunnel deco: replace a few floor tiles with decorated variants
        if (Random.Int(5) == 0) {
            val decoCount = Random.Int(1, 4)
            var placed = 0
            for (y in room.top + 1 until room.bottom) {
                for (x in room.left + 1 until room.right) {
                    val cell = y * Level.WIDTH + x
                    if (level.map[cell] == floor && Random.Int(8) == 0 && placed < decoCount) {
                        level.map[cell] = Terrain.EMPTY_DECO
                        placed++
                    }
                }
                if (placed >= decoCount) break
            }
        }

        for (door in room.connected.values) {
            door?.set(Room.Door.Type.TUNNEL)
        }
    }
}
