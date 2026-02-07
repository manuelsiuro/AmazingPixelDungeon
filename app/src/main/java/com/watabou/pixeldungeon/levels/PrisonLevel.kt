package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Game
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.Emitter
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.mobs.npcs.Wandmaker
import com.watabou.pixeldungeon.effects.Halo
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.utils.Random
open class PrisonLevel : RegularLevel() {
    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }
    override fun tilesTex(): String {
        return Assets.TILES_PRISON
    }
    override fun waterTex(): String {
        return Assets.WATER_PRISON
    }
    override fun water(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.WATER) 0.65f else 0.45f, 4)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(if (feeling === Level.Feeling.GRASS) 0.60f else 0.40f, 3)
    }
    override fun assignRoomType() {
        super.assignRoomType()
        for (r in rooms.orEmpty()) {
            if (r.type == Type.TUNNEL) {
                r.type = Type.PASSAGE
            }
        }
    }
    override fun createMobs() {
        super.createMobs()
        roomEntrance?.let { Wandmaker.Quest.spawn(this, it) }
        com.watabou.pixeldungeon.quests.AiNpcSpawner.spawn(this)
    }
    override fun decorate() {
        // Room-interior prison features
        for (room in rooms.orEmpty()) {
            if (room.type != Type.STANDARD) continue
            if (room.width() <= 3 || room.height() <= 3) continue

            // Cell bars across a middle row
            if (room.width() >= 6 && room.connected.size == 2 && Random.Int(5) == 0) {
                val midY = (room.top + room.bottom) / 2
                val cx = (room.left + room.right) / 2
                for (x in room.left + 2 until room.right - 1) {
                    if (x == cx) continue // gap for passage
                    val cell = midY * WIDTH + x
                    // Check not adjacent to door
                    var nearDoor = false
                    for (door in room.connected.values) {
                        val d = door ?: continue
                        if (kotlin.math.abs(d.x - x) <= 1 && kotlin.math.abs(d.y - midY) <= 1) {
                            nearDoor = true
                            break
                        }
                    }
                    if (!nearDoor && (x - room.left) % 2 == 0) {
                        map[cell] = Terrain.STATUE
                    }
                }
            }

            // Blood stain corners
            val corners = intArrayOf(
                (room.top + 1) * WIDTH + room.left + 1,
                (room.top + 1) * WIDTH + room.right - 1,
                (room.bottom - 1) * WIDTH + room.left + 1,
                (room.bottom - 1) * WIDTH + room.right - 1
            )
            for (corner in corners) {
                if (Random.Float() < 0.3f) {
                    map[corner] = Terrain.EMPTY_DECO
                }
            }
        }

        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map[i] == Terrain.EMPTY) {
                var c = 0.05f
                if (map[i + 1] == Terrain.WALL && map[i + WIDTH] == Terrain.WALL) {
                    c += 0.2f
                }
                if (map[i - 1] == Terrain.WALL && map[i + WIDTH] == Terrain.WALL) {
                    c += 0.2f
                }
                if (map[i + 1] == Terrain.WALL && map[i - WIDTH] == Terrain.WALL) {
                    c += 0.2f
                }
                if (map[i - 1] == Terrain.WALL && map[i - WIDTH] == Terrain.WALL) {
                    c += 0.2f
                }
                if (Random.Float() < c) {
                    map[i] = Terrain.EMPTY_DECO
                }
            }
        }
        for (i in 0 until WIDTH) {
            if (map[i] == Terrain.WALL &&
                (map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
                Random.Int(6) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map[i] == Terrain.WALL &&
                map[i - WIDTH] == Terrain.WALL &&
                (map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
                Random.Int(3) == 0
            ) {
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
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Dark cold water."
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "There are old blood stains on the floor."
            Terrain.BOOKSHELF -> "This is probably a vestige of a prison library. Might it burn?"
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        super.addVisuals(scene)
        addVisuals(this, scene)
    }
    private class Torch(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 1, p.y + 3, 2f, 0f)
            pour(FlameParticle.FACTORY, 0.15f)
            add(Halo(16f, 0xFFFFCC, 0.2f).point(p.x, p.y))
        }
        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }
    }
    companion object {
        fun addVisuals(level: Level, scene: Scene) {
            for (i in 0 until LENGTH) {
                if (level.map[i] == Terrain.WALL_DECO) {
                    scene.add(Torch(i))
                }
            }
        }
    }
}
