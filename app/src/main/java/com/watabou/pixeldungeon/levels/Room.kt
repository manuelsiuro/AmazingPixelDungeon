package com.watabou.pixeldungeon.levels
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.levels.painters.*
import com.watabou.utils.*
import java.lang.reflect.Method
import java.util.*
import com.watabou.utils.Random
class Room : Rect(), Graph.Node, Bundlable {
    var neigbours = HashSet<Room>()
    var connected = HashMap<Room, Door?>()
    var distance = 0
    var price = 1
    var type = Type.NULL
    enum class Type(painter: Class<out Painter>?) {
        NULL(null),
        STANDARD(StandardPainter::class.java),
        ENTRANCE(EntrancePainter::class.java),
        EXIT(ExitPainter::class.java),
        BOSS_EXIT(BossExitPainter::class.java),
        TUNNEL(TunnelPainter::class.java),
        PASSAGE(PassagePainter::class.java),
        SHOP(ShopPainter::class.java),
        BLACKSMITH(BlacksmithPainter::class.java),
        TREASURY(TreasuryPainter::class.java),
        ARMORY(ArmoryPainter::class.java),
        LIBRARY(LibraryPainter::class.java),
        LABORATORY(LaboratoryPainter::class.java),
        VAULT(VaultPainter::class.java),
        TRAPS(TrapsPainter::class.java),
        STORAGE(StoragePainter::class.java),
        MAGIC_WELL(MagicWellPainter::class.java),
        GARDEN(GardenPainter::class.java),
        CRYPT(CryptPainter::class.java),
        STATUE(StatuePainter::class.java),
        POOL(PoolPainter::class.java),
        RAT_KING(RatKingPainter::class.java),
        WEAK_FLOOR(WeakFloorPainter::class.java),
        PIT(PitPainter::class.java),
        WORKSHOP(WorkshopPainter::class.java),
        ALTAR(AltarPainter::class.java),
        ENCHANTING(EnchantingTablePainter::class.java),
        ANVIL_ROOM(AnvilPainter::class.java);
        private var paint: Method? = null
        init {
            if (painter != null) {
                try {
                    paint = painter.getMethod("paint", Level::class.java, Room::class.java)
                } catch (e: Exception) {
                    paint = null
                }
            }
        }
        fun paint(level: Level, room: Room) {
            try {
                paint?.invoke(null, level, room)
            } catch (e: Exception) {
                PixelDungeon.reportException(e)
            }
        }
    }
    fun random(): Int {
        return random(0)
    }
    fun random(m: Int): Int {
        val x = Random.Int(left + 1 + m, right - m)
        val y = Random.Int(top + 1 + m, bottom - m)
        return x + y * Level.WIDTH
    }
    fun addNeigbour(other: Room) {
        val i = intersect(other)
        if ((i.width() == 0 && i.height() >= 3) ||
            (i.height() == 0 && i.width() >= 3)
        ) {
            neigbours.add(other)
            other.neigbours.add(this)
        }
    }
    fun connect(room: Room) {
        if (!connected.containsKey(room)) {
            connected[room] = null
            room.connected[this] = null
        }
    }
    fun entrance(): Door? {
        return connected.values.iterator().next()
    }
    fun inside(p: Int): Boolean {
        val x = p % Level.WIDTH
        val y = p / Level.WIDTH
        return x > left && y > top && x < right && y < bottom
    }
    fun center(): Point {
        return Point(
            (left + right) / 2 + if (((right - left) and 1) == 1) Random.Int(2) else 0,
            (top + bottom) / 2 + if (((bottom - top) and 1) == 1) Random.Int(2) else 0
        )
    }
    // **** Graph.Node interface ****
    override fun distance(): Int {
        return distance
    }
    override fun distance(value: Int) {
        distance = value
    }
    override fun price(): Int {
        return price
    }
    override fun price(value: Int) {
        price = value
    }
    override fun edges(): Collection<Room> {
        return neigbours
    }
    // FIXME: use proper string constants
    override fun storeInBundle(bundle: Bundle) {
        bundle.put("left", left)
        bundle.put("top", top)
        bundle.put("right", right)
        bundle.put("bottom", bottom)
        bundle.put("type", type.toString())
    }
    override fun restoreFromBundle(bundle: Bundle) {
        left = bundle.getInt("left")
        top = bundle.getInt("top")
        right = bundle.getInt("right")
        bottom = bundle.getInt("bottom")
        type = Type.valueOf(bundle.getString("type"))
    }
    class Door(x: Int, y: Int) : Point(x, y) {
        enum class Type {
            EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED
        }
        var type = Type.EMPTY
        fun set(type: Type) {
            if (type.compareTo(this.type) > 0) {
                this.type = type
            }
        }
    }
    companion object {
        val SPECIALS = ArrayList(
            Arrays.asList(
                Type.ARMORY, Type.WEAK_FLOOR, Type.MAGIC_WELL, Type.CRYPT, Type.POOL, Type.GARDEN, Type.LIBRARY,
                Type.TREASURY, Type.TRAPS, Type.STORAGE, Type.STATUE, Type.LABORATORY, Type.VAULT, Type.WORKSHOP, Type.ALTAR,
                Type.ENCHANTING, Type.ANVIL_ROOM
            )
        )
        fun shuffleTypes() {
            val size = SPECIALS.size
            for (i in 0 until size - 1) {
                val j = Random.Int(i, size)
                if (j != i) {
                    val t = SPECIALS[i]
                    SPECIALS[i] = SPECIALS[j]
                    SPECIALS[j] = t
                }
            }
        }
        fun useType(type: Type) {
            if (SPECIALS.remove(type)) {
                SPECIALS.add(type)
            }
        }
        private const val ROOMS = "rooms"
        fun restoreRoomsFromBundle(bundle: Bundle) {
            if (bundle.contains(ROOMS)) {
                SPECIALS.clear()
                val rooms = bundle.getStringArray(ROOMS)
                if (rooms != null) {
                    for (type in rooms) {
                        SPECIALS.add(Type.valueOf(type))
                    }
                }
            } else {
                shuffleTypes()
            }
        }
        fun storeRoomsInBundle(bundle: Bundle) {
            val array = Array(SPECIALS.size) { i -> SPECIALS[i].toString() }
            bundle.put(ROOMS, array)
        }
    }
}
