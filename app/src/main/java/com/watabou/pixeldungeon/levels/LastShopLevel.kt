package com.watabou.pixeldungeon.levels
import com.watabou.noosa.Scene
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.utils.Graph
import com.watabou.utils.Random
class LastShopLevel : RegularLevel() {
    init {
        color1 = 0x4b6636
        color2 = 0xf2f2f2
    }
    override fun tilesTex(): String {
        return Assets.TILES_CITY
    }
    override fun waterTex(): String {
        return Assets.WATER_CITY
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
            val pathSize = Graph.buildPath(activeRooms, currentEntrance, currentExit)?.size ?: return false
            distance = pathSize
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        val entrance = roomEntrance ?: return false
        val exit = roomExit ?: return false
        entrance.type = Type.ENTRANCE
        exit.type = Type.EXIT
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
        var roomShop: Room? = null
        var shopSquare = 0
        for (r in activeRooms) {
            if (r.type == Type.NULL && r.connected.size > 0) {
                r.type = Type.PASSAGE
                if (r.square() > shopSquare) {
                    roomShop = r
                    shopSquare = r.square()
                }
            }
        }
        if (roomShop == null || shopSquare < 30) {
            return false
        } else {
            roomShop.type = if (Imp.Quest.isCompleted()) Room.Type.SHOP else Room.Type.STANDARD
        }
        paint()
        paintWater()
        paintGrass()
        return true
    }
    override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && Random.Int(10) == 0) {
                map[i] = Terrain.EMPTY_DECO
            } else if (map[i] == Terrain.WALL && Random.Int(8) == 0) {
                map[i] = Terrain.WALL_DECO
            } else if (map[i] == Terrain.SECRET_DOOR) {
                map[i] = Terrain.DOOR
            }
        }
        if (Imp.Quest.isCompleted()) {
            while (true) {
                val entranceRoom = roomEntrance ?: break
                val pos = entranceRoom.random()
                if (pos != entrance) {
                    map[pos] = Terrain.SIGN
                    break
                }
            }
        }
    }
    override fun createMobs() {
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
    override fun randomRespawnCell(): Int {
        return -1
    }
    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Suspiciously colored water"
            Terrain.HIGH_GRASS -> "High blooming flowers"
            else -> super.tileName(tile)
        }
    }
    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "A ramp leads up to the upper depth."
            Terrain.EXIT -> "A ramp leads down to the Inferno."
            Terrain.WALL_DECO, Terrain.EMPTY_DECO -> "Several tiles are missing here."
            Terrain.EMPTY_SP -> "Thick carpet covers the floor."
            else -> super.tileDesc(tile)
        }
    }
    override fun water(): BooleanArray {
        return Patch.generate(0.35f, 4)
    }
    override fun grass(): BooleanArray {
        return Patch.generate(0.30f, 3)
    }
    override fun addVisuals(scene: Scene) {
        CityLevel.addVisuals(this, scene)
    }
}
