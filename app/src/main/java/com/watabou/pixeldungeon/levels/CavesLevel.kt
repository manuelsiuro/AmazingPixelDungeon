package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.utils.PointF
import com.watabou.utils.Random
import com.watabou.utils.Rect
open class CavesLevel : RegularLevel() {
    init {
        color1 = 0x534f3e
        color2 = 0xb9d661
        viewDistance = 6
    }
    override fun tilesTex(): String {
        return Assets.TILES_CAVES
    }
    override fun waterTex(): String {
        return Assets.WATER_CAVES
    }
    override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.WATER) 0.60f else 0.45f, 6)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.GRASS) 0.55f else 0.35f, 3)
    }
    override fun assignRoomType() {
        super.assignRoomType()
        rooms?.let { Blacksmith.Quest.spawn(it) }
    }
    override fun createMobs() {
        super.createMobs()
        com.watabou.pixeldungeon.quests.AiNpcSpawner.spawn(this)
    }
    override fun decorate() {
        for (room in rooms.orEmpty()) {
            if (room.type != Type.STANDARD) {
                continue
            }
            if (room.width() <= 3 || room.height() <= 3) {
                continue
            }
            val s = room.square()
            if (Random.Int(s) > 8) {
                val corner = room.left + 1 + (room.top + 1) * WIDTH
                if (map[corner - 1] == Terrain.WALL && map[corner - WIDTH] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner = room.right - 1 + (room.top + 1) * WIDTH
                if (map[corner + 1] == Terrain.WALL && map[corner - WIDTH] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner = room.left + 1 + (room.bottom - 1) * WIDTH
                if (map[corner - 1] == Terrain.WALL && map[corner + WIDTH] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                }
            }
            if (Random.Int(s) > 8) {
                val corner = room.right - 1 + (room.bottom - 1) * WIDTH
                if (map[corner + 1] == Terrain.WALL && map[corner + WIDTH] == Terrain.WALL) {
                    map[corner] = Terrain.WALL
                }
            }
            // Cave rubble
            if (room.width() >= 5 && room.height() >= 5 && Random.Int(5) == 0) {
                val count = Random.Int(2, 5)
                for (i in 0 until count) {
                    val rx = Random.Int(room.left + 2, room.right - 1)
                    val ry = Random.Int(room.top + 2, room.bottom - 1)
                    map[ry * WIDTH + rx] = Terrain.EMBERS
                }
            }

            for (n in room.connected.keys) {
                if ((n.type == Type.STANDARD || n.type == Type.TUNNEL) && Random.Int(3) == 0) {
                    room.connected[n]?.let { Painter.set(this, it, Terrain.EMPTY_DECO) }
                }
            }
        }
        for (i in WIDTH + 1 until LENGTH - WIDTH) {
            if (map[i] == Terrain.EMPTY) {
                var n = 0
                if (map[i + 1] == Terrain.WALL) {
                    n++
                }
                if (map[i - 1] == Terrain.WALL) {
                    n++
                }
                if (map[i + WIDTH] == Terrain.WALL) {
                    n++
                }
                if (map[i - WIDTH] == Terrain.WALL) {
                    n++
                }
                if (Random.Int(6) <= n) {
                    map[i] = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.WALL && Random.Int(12) == 0) {
                map[i] = Terrain.WALL_DECO
            }
        }
        while (true) {
            val entrance = roomEntrance ?: break
            val pos = entrance.random()
            if (pos != this.entrance) {
                map[pos] = Terrain.SIGN
                break
            }
        }
        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            return
        }
        for (r in rooms.orEmpty()) {
            if (r.type == Type.STANDARD) {
                for (n in r.neigbours) {
                    if (n.type == Type.STANDARD && !r.connected.containsKey(n)) {
                        val w = r.intersect(n)
                        if (w.left == w.right && w.bottom - w.top >= 5) {
                            w.top += 2
                            w.bottom -= 1
                            w.right++
                            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.CHASM)
                        } else if (w.top == w.bottom && w.right - w.left >= 5) {
                            w.left += 2
                            w.right -= 1
                            w.bottom++
                            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.CHASM)
                        }
                    }
                }
            }
        }
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.GRASS -> "Fluorescent moss"
            Terrain.HIGH_GRASS -> "Fluorescent mushrooms"
            Terrain.WATER -> "Freezing cold water."
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "The ladder leads up to the upper depth."
            Terrain.EXIT -> "The ladder leads down to the lower depth."
            Terrain.HIGH_GRASS -> "Huge mushrooms block the view."
            Terrain.WALL_DECO -> "A vein of some ore is visible on the wall. Gold?"
            Terrain.BOOKSHELF -> "Who would need a bookshelf in a cave?"
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }
    private class Vein(private val pos: Int) : Group() {
        private var delay: Float = Random.Float(2f)
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                delay -= Game.elapsed
                if (delay <= 0) {
                    delay = Random.Float()
                    val p = DungeonTilemap.tileToWorld(pos)
                    (recycle(Sparkle::class.java) as Sparkle).reset(
                        p.x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                        p.y + Random.Float(DungeonTilemap.SIZE.toFloat())
                    )
                }
            }
        }
    }
    class Sparkle : PixelParticle() {
        fun reset(x: Float, y: Float) {
            revive()
            this.x = x
            this.y = y
            lifespan = 0.5f
            left = lifespan
        }
        override fun update() {
            super.update()
            val p = left / lifespan
            am = (if (p < 0.5f) p * 2 else (1 - p) * 2) * 2
            size(am * 2)
        }
    }

    private class OreVein(private val pos: Int, private val oreColor: Int) : Group() {
        private var delay: Float = Random.Float(2f)
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
                delay -= Game.elapsed
                if (delay <= 0) {
                    delay = Random.Float(1.5f)
                    val p = DungeonTilemap.tileToWorld(pos)
                    val sparkle = recycle(OreSparkle::class.java) as OreSparkle
                    sparkle.reset(
                        p.x + Random.Float(DungeonTilemap.SIZE.toFloat()),
                        p.y + Random.Float(DungeonTilemap.SIZE.toFloat()),
                        oreColor
                    )
                }
            }
        }
    }

    class OreSparkle : PixelParticle() {
        fun reset(x: Float, y: Float, oreColor: Int) {
            revive()
            this.x = x
            this.y = y
            color(oreColor)
            lifespan = 0.6f
            left = lifespan
        }
        override fun update() {
            super.update()
            val p = left / lifespan
            am = (if (p < 0.5f) p * 2 else (1 - p) * 2) * 2
            size(am * 2)
        }
    }

    companion object {
        private const val ORE_COLOR_IRON = 0xCC8844
        private const val ORE_COLOR_GOLD = 0xFFDD00
        private const val ORE_COLOR_DIAMOND = 0xCCEEFF
        private const val ORE_COLOR_ARCANE = 0xAA44FF

        fun oreColor(terrain: Int): Int = when (terrain) {
            Terrain.ORE_WALL_IRON -> ORE_COLOR_IRON
            Terrain.ORE_WALL_GOLD -> ORE_COLOR_GOLD
            Terrain.ORE_WALL_DIAMOND -> ORE_COLOR_DIAMOND
            Terrain.ORE_WALL_ARCANE -> ORE_COLOR_ARCANE
            else -> 0
        }

        fun isOreWall(terrain: Int): Boolean = when (terrain) {
            Terrain.ORE_WALL_IRON, Terrain.ORE_WALL_GOLD,
            Terrain.ORE_WALL_DIAMOND, Terrain.ORE_WALL_ARCANE -> true
            else -> false
        }

        fun addOreVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                val tile = level.map[i]
                if (isOreWall(tile)) {
                    scene.add(OreVein(i, oreColor(tile)))
                }
            }
        }

        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map[i] == Terrain.WALL_DECO) {
                    scene.add(Vein(i))
                }
            }
            addOreVisuals(level, scene)
        }
    }
}
