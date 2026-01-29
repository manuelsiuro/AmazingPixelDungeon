package com.watabou.pixeldungeon.levels
import com.watabou.pixeldungeon.Bones
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.levels.Room.Type
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.utils.Bundle
import com.watabou.utils.Graph
import com.watabou.utils.Random
import com.watabou.utils.Rect
import java.util.ArrayList
import java.util.HashSet
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
abstract class RegularLevel : Level() {
    var rooms: HashSet<Room>? = null
    var roomEntrance: Room? = null
    var roomExit: Room? = null
    protected var specials: ArrayList<Type> = ArrayList()
    var secretDoors: Int = 0
    protected var minRoomSize: Int = 7
    protected var maxRoomSize: Int = 9
    override fun build(): Boolean {
        if (!initRooms()) {
            return false
        }
        val rooms = this.rooms ?: return false
        var distance: Int
        var retry = 0
        val minDistance = sqrt(rooms.size.toDouble()).toInt()
        do {
            do {
                roomEntrance = Random.element(rooms)
            } while (roomEntrance?.let { it.width() < 4 || it.height() < 4 } == true)
            do {
                roomExit = Random.element(rooms)
            } while (roomExit === roomEntrance || roomExit?.let { it.width() < 4 || it.height() < 4 } == true)
            val currentExit = roomExit ?: return false
            val currentEntrance = roomEntrance ?: return false
            Graph.buildDistanceMap(rooms, currentExit)
            distance = currentEntrance.distance()
            if (retry++ > 10) {
                return false
            }
        } while (distance < minDistance)
        val entrance = roomEntrance ?: return false
        val exit = roomExit ?: return false
        entrance.type = Type.ENTRANCE
        exit.type = Type.EXIT
        val connected = HashSet<Room>()
        connected.add(entrance)
        Graph.buildDistanceMap(rooms, exit)
        var path = Graph.buildPath(rooms, entrance, exit) ?: return false
        var room: Room = entrance
        for (next in path) {
            room.connect(next)
            room = next
            connected.add(room)
        }
        Graph.setPrice(path, entrance.distance)
        Graph.buildDistanceMap(rooms, exit)
        path = Graph.buildPath(rooms, entrance, exit) ?: return false
        room = entrance
        for (next in path) {
            room.connect(next)
            room = next
            connected.add(room)
        }
        val nConnected = (rooms.size * Random.Float(0.5f, 0.7f)).toInt()
        while (connected.size < nConnected) {
            val cr = Random.element(connected) ?: continue
            val or = Random.element(cr.neigbours) ?: continue
            if (!connected.contains(or)) {
                cr.connect(or)
                connected.add(or)
            }
        }
        if (Dungeon.shopOnLevel()) {
            var shop: Room? = null
            for (r in entrance.connected.keys) {
                if (r.connected.size == 1 && r.width() >= 5 && r.height() >= 5) {
                    shop = r
                    break
                }
            }
            if (shop == null) {
                return false
            } else {
                shop.type = Type.SHOP
            }
        }
        specials = ArrayList(Room.SPECIALS)
        if (Dungeon.bossLevel(Dungeon.depth + 1)) {
            specials.remove(Type.WEAK_FLOOR)
        }
        assignRoomType()
        paint()
        paintWater()
        paintGrass()
        placeTraps()
        return true
    }
    protected fun initRooms(): Boolean {
        rooms = HashSet()
        split(Rect(0, 0, WIDTH - 1, HEIGHT - 1))
        val currentRooms = rooms ?: return false
        if (currentRooms.size < 8) {
            return false
        }
        val ra = currentRooms.toTypedArray()
        for (i in 0 until ra.size - 1) {
            for (j in i + 1 until ra.size) {
                ra[i].addNeigbour(ra[j])
            }
        }
        return true
    }
    protected open fun assignRoomType() {
        var specialRooms = 0
        for (r in rooms.orEmpty()) {
            if (r.type == Type.NULL && r.connected.size == 1) {
                if (specials.size > 0 &&
                    r.width() > 3 && r.height() > 3 &&
                    Random.Int(specialRooms * specialRooms + 2) == 0
                ) {
                    if (pitRoomNeeded) {
                        r.type = Type.PIT
                        pitRoomNeeded = false
                        specials.remove(Type.ARMORY)
                        specials.remove(Type.CRYPT)
                        specials.remove(Type.LABORATORY)
                        specials.remove(Type.LIBRARY)
                        specials.remove(Type.STATUE)
                        specials.remove(Type.TREASURY)
                        specials.remove(Type.VAULT)
                        specials.remove(Type.WEAK_FLOOR)
                    } else if (Dungeon.depth % 5 == 2 && specials.contains(Type.LABORATORY)) {
                        r.type = Type.LABORATORY
                    } else {
                        val n = specials.size
                        r.type = specials[min(Random.Int(n), Random.Int(n))]
                        if (r.type == Type.WEAK_FLOOR) {
                            weakFloorCreated = true
                        }
                    }
                    Room.useType(r.type)
                    specials.remove(r.type)
                    specialRooms++
                } else if (Random.Int(2) == 0) {
                    val neigbours = HashSet<Room>()
                    for (n in r.neigbours) {
                        if (!r.connected.containsKey(n) &&
                            !Room.SPECIALS.contains(n.type) &&
                            n.type != Type.PIT
                        ) {
                            neigbours.add(n)
                        }
                    }
                    if (neigbours.size > 1) {
                        Random.element(neigbours)?.let { r.connect(it) }
                    }
                }
            }
        }
        var count = 0
        for (r in rooms.orEmpty()) {
            if (r.type == Type.NULL) {
                val connections = r.connected.size
                if (connections == 0) {
                } else if (Random.Int(connections * connections) == 0) {
                    r.type = Type.STANDARD
                    count++
                } else {
                    r.type = Type.TUNNEL
                }
            }
        }
        while (count < 4) {
            val r = randomRoom(Type.TUNNEL, 1)
            if (r != null) {
                r.type = Type.STANDARD
                count++
            }
        }
    }
    protected open fun paintWater() {
        val lake = water()
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.EMPTY && lake[i]) {
                map[i] = Terrain.WATER
            }
        }
    }
    protected open fun paintGrass() {
        val grass = grass()
        if (feeling === Level.Feeling.GRASS) {
            for (room in rooms.orEmpty()) {
                if (room.type != Type.NULL && room.type != Type.PASSAGE && room.type != Type.TUNNEL) {
                    grass[room.left + 1 + (room.top + 1) * WIDTH] = true
                    grass[room.right - 1 + (room.top + 1) * WIDTH] = true
                    grass[room.left + 1 + (room.bottom - 1) * WIDTH] = true
                    grass[room.right - 1 + (room.bottom - 1) * WIDTH] = true
                }
            }
        }
        for (i in WIDTH + 1 until LENGTH - WIDTH - 1) {
            if (map[i] == Terrain.EMPTY && grass[i]) {
                var count = 1
                for (n in Level.NEIGHBOURS8) {
                    if (grass[i + n]) {
                        count++
                    }
                }
                map[i] = if (Random.Float() < count / 12f) Terrain.HIGH_GRASS else Terrain.GRASS
            }
        }
    }
    protected abstract fun water(): BooleanArray
    protected abstract fun grass(): BooleanArray
    protected open fun placeTraps() {
        val nTraps = nTraps()
        val trapChances = trapChances()
        for (i in 0 until nTraps) {
            val trapPos = Random.Int(LENGTH)
            if (map[trapPos] == Terrain.EMPTY) {
                when (Random.chances(trapChances)) {
                    0 -> map[trapPos] = Terrain.SECRET_TOXIC_TRAP
                    1 -> map[trapPos] = Terrain.SECRET_FIRE_TRAP
                    2 -> map[trapPos] = Terrain.SECRET_PARALYTIC_TRAP
                    3 -> map[trapPos] = Terrain.SECRET_POISON_TRAP
                    4 -> map[trapPos] = Terrain.SECRET_ALARM_TRAP
                    5 -> map[trapPos] = Terrain.SECRET_LIGHTNING_TRAP
                    6 -> map[trapPos] = Terrain.SECRET_GRIPPING_TRAP
                    7 -> map[trapPos] = Terrain.SECRET_SUMMONING_TRAP
                }
            }
        }
    }
    protected open fun nTraps(): Int {
        return if (Dungeon.depth <= 1) 0 else Random.Int(1, (rooms?.size ?: 0) + Dungeon.depth)
    }
    protected open fun trapChances(): FloatArray {
        return floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
    }
    protected fun split(rect: Rect) {
        val w = rect.width()
        val h = rect.height()
        if (w > maxRoomSize && h < minRoomSize) {
            val vw = Random.Int(rect.left + 3, rect.right - 3)
            split(Rect(rect.left, rect.top, vw, rect.bottom))
            split(Rect(vw, rect.top, rect.right, rect.bottom))
        } else if (h > maxRoomSize && w < minRoomSize) {
            val vh = Random.Int(rect.top + 3, rect.bottom - 3)
            split(Rect(rect.left, rect.top, rect.right, vh))
            split(Rect(rect.left, vh, rect.right, rect.bottom))
        } else if (Math.random() <= (minRoomSize * minRoomSize / rect.square()).toDouble() && w <= maxRoomSize && h <= maxRoomSize || w < minRoomSize || h < minRoomSize) {
            rooms?.add(Room().set(rect) as Room)
        } else {
            if (Random.Float() < (w - 2).toFloat() / (w + h - 4)) {
                val vw = Random.Int(rect.left + 3, rect.right - 3)
                split(Rect(rect.left, rect.top, vw, rect.bottom))
                split(Rect(vw, rect.top, rect.right, rect.bottom))
            } else {
                val vh = Random.Int(rect.top + 3, rect.bottom - 3)
                split(Rect(rect.left, rect.top, rect.right, vh))
                split(Rect(rect.left, vh, rect.right, rect.bottom))
            }
        }
    }
    protected open fun paint() {
        for (r in rooms.orEmpty()) {
            if (r.type != Type.NULL) {
                placeDoors(r)
                r.type.paint(this, r)
            } else {
                if (feeling === Level.Feeling.CHASM && Random.Int(2) == 0) {
                    Painter.fill(this, r, Terrain.WALL)
                }
            }
        }
        for (r in rooms.orEmpty()) {
            paintDoors(r)
        }
    }
    private fun placeDoors(r: Room) {
        for (n in r.connected.keys) {
            var door: Room.Door? = r.connected[n]
            if (door == null) {
                val i = r.intersect(n)
                if (i.width() == 0) {
                    door = Room.Door(
                        i.left,
                        Random.Int(i.top + 1, i.bottom)
                    )
                } else {
                    door = Room.Door(
                        Random.Int(i.left + 1, i.right),
                        i.top
                    )
                }
                r.connected[n] = door
                n.connected[r] = door
            }
        }
    }
    protected open fun paintDoors(r: Room) {
        for (n in r.connected.keys) {
            if (joinRooms(r, n)) {
                continue
            }
            val doorPoint = r.connected[n] ?: continue
            val door = doorPoint.x + doorPoint.y * WIDTH
            when (doorPoint.type) {
                Room.Door.Type.EMPTY -> map[door] = Terrain.EMPTY
                Room.Door.Type.TUNNEL -> map[door] = tunnelTile()
                Room.Door.Type.REGULAR -> if (Dungeon.depth <= 1) {
                    map[door] = Terrain.DOOR
                } else {
                    val secret = (if (Dungeon.depth < 6) Random.Int(12 - Dungeon.depth) else Random.Int(6)) == 0
                    map[door] = if (secret) Terrain.SECRET_DOOR else Terrain.DOOR
                    if (secret) {
                        secretDoors++
                    }
                }
                Room.Door.Type.UNLOCKED -> map[door] = Terrain.DOOR
                Room.Door.Type.HIDDEN -> {
                    map[door] = Terrain.SECRET_DOOR
                    secretDoors++
                }
                Room.Door.Type.BARRICADE -> map[door] = if (Random.Int(3) == 0) Terrain.BOOKSHELF else Terrain.BARRICADE
                Room.Door.Type.LOCKED -> map[door] = Terrain.LOCKED_DOOR
            }
        }
    }
    protected open fun joinRooms(r: Room, n: Room): Boolean {
        if (r.type != Type.STANDARD || n.type != Type.STANDARD) {
            return false
        }
        val w = r.intersect(n)
        if (w.left == w.right) {
            if (w.bottom - w.top < 3) {
                return false
            }
            if (w.height() == max(r.height(), n.height())) {
                return false
            }
            if (r.width() + n.width() > maxRoomSize) {
                return false
            }
            w.top += 1
            w.bottom -= 0
            w.right++
            Painter.fill(this, w.left, w.top, 1, w.height(), Terrain.EMPTY)
        } else {
            if (w.right - w.left < 3) {
                return false
            }
            if (w.width() == max(r.width(), n.width())) {
                return false
            }
            if (r.height() + n.height() > maxRoomSize) {
                return false
            }
            w.left += 1
            w.right -= 0
            w.bottom++
            Painter.fill(this, w.left, w.top, w.width(), 1, Terrain.EMPTY)
        }
        return true
    }
    override fun nMobs(): Int {
        return 2 + Dungeon.depth % 5 + Random.Int(3)
    }
    override fun createMobs() {
        val nMobs = nMobs()
        for (i in 0 until nMobs) {
            val mob = Bestiary.mob(Dungeon.depth)
            if (mob == null) continue
            do {
                mob.pos = randomRespawnCell()
            } while (mob.pos == -1)
            mobs.add(mob)
            Actor.occupyCell(mob)
        }
    }
    override fun randomRespawnCell(): Int {
        var count = 0
        var cell: Int
        while (true) {
            if (++count > 10) {
                return -1
            }
            val room = randomRoom(Type.STANDARD, 10) ?: continue
            cell = room.random()
            if (!Dungeon.visible[cell] && Actor.findChar(cell) == null && Level.passable[cell]) {
                return cell
            }
        }
    }
    override fun randomDestination(): Int {
        var cell: Int
        while (true) {
            val room = Random.element(rooms.orEmpty()) ?: continue
            cell = room.random()
            if (Level.passable[cell]) {
                return cell
            }
        }
    }
    override fun createItems() {
        var nItems = 3
        while (Random.Float() < 0.4f) {
            nItems++
        }
        for (i in 0 until nItems) {
            var type: Heap.Type
            when (Random.Int(20)) {
                0 -> type = Heap.Type.SKELETON
                1, 2, 3, 4 -> type = Heap.Type.CHEST
                5 -> type = if (Dungeon.depth > 1) Heap.Type.MIMIC else Heap.Type.CHEST
                else -> type = Heap.Type.HEAP
            }
            Generator.random()?.let { drop(it, randomDropCell()).type = type }
        }
        for (item in itemsToSpawn) {
            var cell = randomDropCell()
            if (item is ScrollOfUpgrade) {
                while (map[cell] == Terrain.FIRE_TRAP || map[cell] == Terrain.SECRET_FIRE_TRAP) {
                    cell = randomDropCell()
                }
            }
            drop(item, cell).type = Heap.Type.HEAP
        }
        val item = Bones.get()
        if (item != null) {
            drop(item, randomDropCell()).type = Heap.Type.SKELETON
        }
    }
    protected fun randomRoom(type: Type, tries: Int): Room? {
        for (i in 0 until tries) {
            val room = Random.element(rooms.orEmpty()) ?: continue
            if (room.type == type) {
                return room
            }
        }
        return null
    }
    fun room(pos: Int): Room? {
        for (room in rooms.orEmpty()) {
            if (room.type != Type.NULL && room.inside(pos)) {
                return room
            }
        }
        return null
    }
    protected fun randomDropCell(): Int {
        while (true) {
            val room = randomRoom(Type.STANDARD, 1)
            if (room != null) {
                val pos = room.random()
                if (passable[pos]) {
                    return pos
                }
            }
        }
    }
    override fun pitCell(): Int {
        for (room in rooms.orEmpty()) {
            if (room.type == Type.PIT) {
                return room.random()
            }
        }
        return super.pitCell()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        rooms?.let { bundle.put("rooms", it) }
    }
    @Suppress("UNCHECKED_CAST")
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        rooms = HashSet(bundle.getCollection("rooms") as Collection<Room>)
        for (r in rooms.orEmpty()) {
            if (r.type == Type.WEAK_FLOOR) {
                weakFloorCreated = true
                break
            }
        }
    }
}
