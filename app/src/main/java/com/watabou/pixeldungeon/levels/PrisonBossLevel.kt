package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.IronKey
import com.watabou.pixeldungeon.items.keys.SkeletonKey
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Graph
import com.watabou.utils.Point
import com.watabou.utils.Random
class PrisonBossLevel : RegularLevel() {
    init {
        color1 = 0x6a723d
        color2 = 0x88924c
    }
    private lateinit var anteroom: Room
    private var arenaDoor: Int = 0
    private var enteredArena = false
    private var keyDropped = false
    override fun tilesTex(): String {
        return Assets.TILES_PRISON
    }
    override fun waterTex(): String {
        return Assets.WATER_PRISON
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ARENA, roomExit)
        bundle.put(DOOR, arenaDoor)
        bundle.put(ENTERED, enteredArena)
        bundle.put(DROPPED, keyDropped)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        roomExit = bundle.get(ARENA) as Room
        arenaDoor = bundle.getInt(DOOR)
        enteredArena = bundle.getBoolean(ENTERED)
        keyDropped = bundle.getBoolean(DROPPED)
    }
    override fun build(): Boolean {
        initRooms()
        val activeRooms = this.rooms ?: return false
        var distance: Int
        var retry = 0
        do {
            if (retry++ > 10) {
                return false
            }
            var innerRetry = 0
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomEntrance = Random.element(activeRooms)
            } while (roomEntrance?.let { it.width() < 4 || it.height() < 4 } == true)
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomExit = Random.element(activeRooms)
            } while (
                roomExit === roomEntrance ||
                roomExit?.let { it.width() < 7 || it.height() < 7 || it.top == 0 } == true
            )
            val currentExit = roomExit ?: return false
            val currentEntrance = roomEntrance ?: return false
            Graph.buildDistanceMap(activeRooms, currentExit)
            distance = Graph.buildPath(activeRooms, currentEntrance, currentExit)?.size ?: return false
        } while (distance < 3)
        val entrance = roomEntrance ?: return false
        val exit = roomExit ?: return false
        entrance.type = Type.ENTRANCE
        exit.type = Type.BOSS_EXIT
        var path = Graph.buildPath(activeRooms, entrance, exit) ?: return false
        Graph.setPrice(path, entrance.distance)
        Graph.buildDistanceMap(activeRooms, exit)
        path = Graph.buildPath(activeRooms, entrance, exit) ?: return false
        anteroom = path[path.size - 2]
        anteroom.type = Type.STANDARD
        var room: Room = entrance
        for (next in path) {
            room.connect(next)
            room = next
        }
        for (r in activeRooms) {
            if (r.type == Type.NULL && r.connected.size > 0) {
                r.type = Type.PASSAGE
            }
        }
        paint()
        val r = exit.connected.keys.toTypedArray()[0]
        if (exit.connected[r]?.y == exit.top) {
            return false
        }
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }
    override fun water(): BooleanArray {
        return Patch.generate(0.45f, 5)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(0.30f, 4)
    }
    override fun paintDoors(r: Room) {
        for (n in r.connected.keys) {
            if (r.type == Type.NULL) {
                continue
            }
            val door = r.connected[n] ?: continue
            if (r.type == Room.Type.PASSAGE && n.type == Room.Type.PASSAGE) {
                Painter.set(this, door, Terrain.EMPTY)
            } else {
                Painter.set(this, door, Terrain.DOOR)
            }
        }
    }
    override fun placeTraps() {
        val nTraps = nTraps()
        for (i in 0 until nTraps) {
            val trapPos = Random.Int(LENGTH)
            if (map[trapPos] == Terrain.EMPTY) {
                map[trapPos] = Terrain.POISON_TRAP
            }
        }
    }
    override fun decorate() {
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map[i] == Terrain.EMPTY) {
                var c = 0.15f
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
                Random.Int(4) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        for (i in WIDTH until LENGTH - WIDTH) {
            if (map[i] == Terrain.WALL &&
                map[i - WIDTH] == Terrain.WALL &&
                (map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
                Random.Int(2) == 0
            ) {
                map[i] = Terrain.WALL_DECO
            }
        }
        while (true) {
            val entranceRoom = roomEntrance ?: break
            val pos = entranceRoom.random()
            if (pos != entrance) {
                map[pos] = Terrain.SIGN
                break
            }
        }
        val exitRoom = roomExit ?: return
        val door = exitRoom.entrance() ?: return
        arenaDoor = door.x + door.y * WIDTH
        Painter.set(this, arenaDoor, Terrain.LOCKED_DOOR)
        Painter.fill(
            this,
            exitRoom.left + 2,
            exitRoom.top + 2,
            exitRoom.width() - 3,
            exitRoom.height() - 3,
            Terrain.INACTIVE_TRAP
        )
    }
    override fun createMobs() {
    }
    override fun respawner(): Actor? {
        return null
    }
    override fun createItems() {
        var keyPos = anteroom.random()
        while (!passable[keyPos]) {
            keyPos = anteroom.random()
        }
        drop(IronKey(), keyPos).type = Heap.Type.CHEST
        val item = Bones.get() ?: return
        val entranceRoom = roomEntrance ?: return
        var pos: Int
        do {
            pos = entranceRoom.random()
        } while (pos == entrance || map[pos] == Terrain.SIGN)
        drop(item, pos).type = Heap.Type.SKELETON
    }
    override fun press(cell: Int, ch: Char?) {
        val localChar = ch ?: return
        super.press(cell, localChar)
        val exitRoom = roomExit ?: return
        if (localChar === Dungeon.hero && !enteredArena && exitRoom.inside(cell)) {
            enteredArena = true
            var pos: Int
            do {
                pos = exitRoom.random()
            } while (pos == cell || Actor.findChar(pos) != null)
            val boss = Bestiary.mob(Dungeon.depth) ?: return
            boss.state = boss.HUNTING
            boss.pos = pos
            GameScene.add(boss)
            boss.notice()
            mobPress(boss)
            set(arenaDoor, Terrain.LOCKED_DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
    }
    override fun drop(item: Item, cell: Int): Heap {
        if (!keyDropped && item is SkeletonKey) {
            keyDropped = true
            set(arenaDoor, Terrain.DOOR)
            GameScene.updateMap(arenaDoor)
            Dungeon.observe()
        }
        return super.drop(item, cell)
    }
    override fun randomRespawnCell(): Int {
        return -1
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
            else -> super.tileDesc(tile)
        }
    }
    override fun addVisuals(scene: Scene) {
        PrisonLevel.addVisuals(this, scene)
    }
    companion object {
        private const val ARENA = "arena"
        private const val DOOR = "door"
        private const val ENTERED = "entered"
        private const val DROPPED = "droppped"
    }
}
