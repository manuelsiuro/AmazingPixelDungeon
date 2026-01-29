package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Bundle
import com.watabou.utils.Graph
import com.watabou.utils.Random
import java.util.ArrayList
class SewerBossLevel : RegularLevel() {
    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }
    private var stairs = 0
    override fun tilesTex(): String {
        return Assets.TILES_SEWERS
    }
    override fun waterTex(): String {
        return Assets.WATER_SEWERS
    }
    override fun build(): Boolean {
        initRooms()
        val activeRooms = this.rooms ?: return false
        var distance: Int
        var retry = 0
        val minDistance = Math.sqrt(activeRooms.size.toDouble()).toInt()
        do {
            var innerRetry = 0
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomEntrance = Random.element(activeRooms)
            } while (roomEntrance?.let { it.width() < 4 || it.height() < 4 } == true)
            innerRetry = 0
            do {
                if (innerRetry++ > 10) {
                    return false
                }
                roomExit = Random.element(activeRooms)
            } while (roomExit === roomEntrance || roomExit?.let { it.width() < 6 || it.height() < 6 || it.top == 0 } == true)
            val currentExit = roomExit ?: return false
            val currentEntrance = roomEntrance ?: return false
            Graph.buildDistanceMap(activeRooms, currentExit)
            distance = currentEntrance.distance()
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        val entrance = roomEntrance ?: return false
        val exit = roomExit ?: return false
        entrance.type = Type.ENTRANCE
        exit.type = Type.BOSS_EXIT
        Graph.buildDistanceMap(activeRooms, exit)
        var path = Graph.buildPath(activeRooms, entrance, exit) ?: return false
        Graph.setPrice(path, entrance.distance)
        Graph.buildDistanceMap(activeRooms, exit)
        path = Graph.buildPath(activeRooms, entrance, exit) ?: return false
        var room: Room = entrance
        for (next in path) {
            room.connect(next)
            room = next
        }
        room = exit.connected.keys.toTypedArray()[0]
        if (exit.top == room.bottom) {
            return false
        }
        for (r in activeRooms) {
            if (r.type == Type.NULL && r.connected.size > 0) {
                r.type = Type.TUNNEL
            }
        }
        val candidates = ArrayList<Room>()
        for (r in exit.neigbours) {
            if (!exit.connected.containsKey(r) &&
                (exit.left == r.right || exit.right == r.left || exit.bottom == r.top)
            ) {
                candidates.add(r)
            }
        }
        if (candidates.size > 0) {
            val kingsRoom = Random.element(candidates) ?: return false
            kingsRoom.connect(exit)
            kingsRoom.type = Room.Type.RAT_KING
        }
        paint()
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }
    override fun water(): BooleanArray {
        return Patch.generate(0.5f, 5)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(0.40f, 4)
    }
    override fun decorate() {
        val exitRoom = roomExit ?: return
        val start = exitRoom.top * WIDTH + exitRoom.left + 1
        val end = start + exitRoom.width() - 1
        for (i in start until end) {
            if (i != exit) {
                map[i] = Terrain.WALL_DECO
                map[i + WIDTH] = Terrain.WATER
            } else {
                map[i + WIDTH] = Terrain.EMPTY
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
    }
    override fun addVisuals(scene: Scene) {
        SewerLevel.addVisuals(this, scene)
    }
    override fun createMobs() {
        val mob = Bestiary.mob(Dungeon.depth) ?: return
        val exitRoom = roomExit ?: return
        mob.pos = exitRoom.random()
        mobs.add(mob)
    }
    override fun respawner(): Actor? {
        return null
    }
    override fun createItems() {
        val item = Bones.get() ?: return
        val entranceRoom = roomEntrance ?: return
        var pos: Int
        do {
            pos = entranceRoom.random()
        } while (pos == entrance || map[pos] == Terrain.SIGN)
        drop(item, pos).type = Heap.Type.SKELETON
    }
    fun seal() {
        if (entrance != 0) {
            set(entrance, Terrain.WATER_TILES)
            GameScene.updateMap(entrance)
            GameScene.ripple(entrance)
            stairs = entrance
            entrance = 0
        }
    }
    fun unseal() {
        if (stairs != 0) {
            entrance = stairs
            stairs = 0
            set(entrance, Terrain.ENTRANCE)
            GameScene.updateMap(entrance)
        }
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(STAIRS, stairs)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        stairs = bundle.getInt(STAIRS)
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Murky water"
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.EMPTY_DECO -> "Wet yellowish moss covers the floor."
            else -> super.tileDesc(tile)
        }
    }
    companion object {
        private const val STAIRS = "stairs"
    }
}
