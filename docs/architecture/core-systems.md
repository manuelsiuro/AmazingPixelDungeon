# Core Systems

This document details the fundamental systems that power Amazing Pixel Dungeon.

## Game Loop

### Frame Timing
The game targets 30 FPS with variable timestep:

```kotlin
// In Game.kt
private var lastTime: Long = 0
private const val FRAME_TIME = 1000f / 30  // ~33ms per frame

fun step() {
    val now = SystemClock.elapsedRealtime()
    val delta = (now - lastTime) / 1000f
    lastTime = now

    // Clamp delta to prevent spiral of death
    val elapsed = minOf(delta, 0.1f)

    // Update game logic
    scene?.update(elapsed)
    Actor.process()
}
```

### Update Cycle
Each frame follows this sequence:

1. **Input Processing**: Touch and key events queued and processed
2. **Scene Update**: Cascades through visual hierarchy
3. **Actor Processing**: Turn-based entities execute actions
4. **Animation Update**: Sprite frames advance
5. **Effect Update**: Particles and visual effects
6. **Camera Update**: Follow hero, screen shake

### Rendering Cycle
```kotlin
fun draw() {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

    camera.updateMatrix()
    scene?.draw()  // Recursively draws all children
}
```

## State Management

### Dungeon Singleton
`Dungeon.kt` is the authoritative source of game state:

```kotlin
object Dungeon {
    // Current game state
    var level: Level? = null
    var hero: Hero? = null
    var depth: Int = 0
    var gold: Int = 0

    // Game progress
    var challenges: Int = 0
    var chapters: HashSet<Int> = hashSetOf()

    // Level history for backtracking
    var droppedItems: SparseArray<ArrayList<Item>> = SparseArray()

    // Statistics
    var potionOfStrength: Int = 0
    var scrollsOfUpgrade: Int = 0
    var arcaneStyli: Int = 0
}
```

### State Transitions
```
┌──────────────┐
│  New Game    │
│  Dungeon.    │
│  init()      │
└──────┬───────┘
       │
       ▼
┌──────────────┐    ┌──────────────┐
│ Dungeon.     │◀──▶│ Dungeon.     │
│ newLevel()   │    │ loadLevel()  │
└──────┬───────┘    └──────────────┘
       │
       ▼
┌──────────────┐
│  Active      │
│  Gameplay    │
└──────┬───────┘
       │
   ┌───┴───┐
   ▼       ▼
Death    Victory
```

## Save/Load System

### File Structure
```
/data/data/com.watabou.pixeldungeon/files/
├── game.dat           # Active game state
├── warrior.dat        # Warrior class save
├── mage.dat          # Mage class save
├── rogue.dat         # Rogue class save
├── huntress.dat      # Huntress class save
├── depth{n}.dat      # Individual level saves
└── rankings.dat      # High scores
```

### Bundle Serialization

The `Bundle` class provides type-safe serialization:

```kotlin
class Bundle {
    private val data = HashMap<String, Any>()

    // Primitives
    fun put(key: String, value: Int)
    fun put(key: String, value: Float)
    fun put(key: String, value: Boolean)
    fun put(key: String, value: String)

    // Arrays
    fun put(key: String, value: IntArray)
    fun put(key: String, value: BooleanArray)

    // Objects implementing Bundlable
    fun put(key: String, value: Bundlable)
    fun put(key: String, value: Collection<Bundlable>)

    // Retrieval
    fun getInt(key: String): Int
    fun getFloat(key: String): Float
    fun getBoolean(key: String): Boolean
    fun getString(key: String): String
    fun getBundlable(key: String): Bundlable?
    fun getCollection(key: String): Collection<Bundlable>
}
```

### Saving Game State

```kotlin
// In Dungeon.kt
fun saveGame(fileName: String) {
    val bundle = Bundle()

    // Save hero
    bundle.put(HERO, hero)

    // Save level reference
    bundle.put(DEPTH, depth)
    bundle.put(GOLD, gold)

    // Save challenges
    bundle.put(CHALLENGES, challenges)

    // Write to file
    val output = FileOutputStream(fileName)
    Bundle.write(bundle, output)
    output.close()
}

fun loadGame(fileName: String): Boolean {
    val bundle = Bundle.read(FileInputStream(fileName))

    hero = bundle.get(HERO) as Hero
    depth = bundle.getInt(DEPTH)
    gold = bundle.getInt(GOLD)
    challenges = bundle.getInt(CHALLENGES)

    return true
}
```

### Level Serialization

Each level saves independently:

```kotlin
// Level.kt
override fun storeInBundle(bundle: Bundle) {
    bundle.put(MAP, map)              // Terrain array
    bundle.put(VISITED, visited)      // Explored cells
    bundle.put(MAPPED, mapped)        // Magic mapped cells
    bundle.put(MOBS, mobs)            // Enemy list
    bundle.put(HEAPS, heaps.values()) // Items on ground
    bundle.put(PLANTS, plants.values())
    bundle.put(TRAPS, traps.values())
}
```

## Asset Management

### Assets.kt
Centralized asset path definitions:

```kotlin
object Assets {
    // UI
    const val ARCS_BG = "arcs1.png"
    const val ARCS_FG = "arcs2.png"
    const val DASHBOARD = "dashboard.png"

    // Characters
    const val WARRIOR = "warrior.png"
    const val MAGE = "mage.png"
    const val ROGUE = "rogue.png"
    const val HUNTRESS = "huntress.png"

    // Enemies
    const val RAT = "rat.png"
    const val GNOLL = "gnoll.png"
    const val GOO = "goo.png"
    // ... etc

    // Tilesets
    const val TILES_SEWERS = "tiles0.png"
    const val TILES_PRISON = "tiles1.png"
    const val TILES_CAVES = "tiles2.png"
    const val TILES_CITY = "tiles3.png"
    const val TILES_HALLS = "tiles4.png"
}
```

### Texture Loading
```kotlin
// TextureCache.kt
object TextureCache {
    private val cache = HashMap<Any, SmartTexture>()

    fun get(src: Any): SmartTexture {
        return cache.getOrPut(src) {
            when (src) {
                is String -> SmartTexture(Bitmap.load(src))
                is Bitmap -> SmartTexture(src)
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun clear() {
        cache.values.forEach { it.delete() }
        cache.clear()
    }
}
```

## Preferences System

### Preferences.kt
Game settings persistence:

```kotlin
object Preferences {
    private lateinit var prefs: SharedPreferences

    // Display
    var landscape: Boolean
    var scale: Int  // 0=auto, 1-3=fixed
    var brightness: Int  // -2 to 2

    // Audio
    var music: Boolean
    var soundFx: Boolean

    // Gameplay
    var lastClass: Int  // Last played hero class
    var challenges: Int  // Active challenges bitmask
    var intro: Boolean  // Show intro

    fun put(key: String, value: Any) {
        prefs.edit().apply {
            when (value) {
                is Int -> putInt(key, value)
                is Boolean -> putBoolean(key, value)
                is String -> putString(key, value)
            }
            apply()
        }
    }
}
```

## Challenge System

Challenges modify game rules for increased difficulty:

```kotlin
object Challenges {
    const val NO_FOOD        = 0x01  // No food drops
    const val NO_ARMOR       = 0x02  // No armor drops
    const val NO_HEALING     = 0x04  // No healing potions
    const val NO_HERBALISM   = 0x08  // No seeds/plants
    const val SWARM_INTEL    = 0x10  // Smarter enemies
    const val DARKNESS       = 0x20  // Reduced vision

    fun isEnabled(challenge: Int): Boolean {
        return Dungeon.challenges and challenge != 0
    }
}
```

## Badge System

Achievement tracking in `Badges.kt`:

```kotlin
enum class Badge {
    // Progression
    MONSTERS_SLAIN_1,
    MONSTERS_SLAIN_2,
    GOLD_COLLECTED_1,
    GOLD_COLLECTED_2,

    // Boss kills
    BOSS_SLAIN_1,  // Goo
    BOSS_SLAIN_2,  // Tengu
    BOSS_SLAIN_3,  // DM-300
    BOSS_SLAIN_4,  // King

    // Victory
    VICTORY,
    HAPPY_END,
    CHAMPION,
    // ... etc
}

object Badges {
    private val global = HashSet<Badge>()
    private val local = HashSet<Badge>()

    fun validateMonstersSlain() {
        when {
            Statistics.enemiesSlain >= 1000 -> unlock(MONSTERS_SLAIN_4)
            Statistics.enemiesSlain >= 500 -> unlock(MONSTERS_SLAIN_3)
            Statistics.enemiesSlain >= 100 -> unlock(MONSTERS_SLAIN_2)
            Statistics.enemiesSlain >= 10 -> unlock(MONSTERS_SLAIN_1)
        }
    }

    private fun unlock(badge: Badge) {
        if (badge !in global) {
            global.add(badge)
            local.add(badge)
            displayBadge(badge)
        }
    }
}
```

## Statistics Tracking

```kotlin
object Statistics {
    var goldCollected: Int = 0
    var deepestFloor: Int = 0
    var enemiesSlain: Int = 0
    var foodEaten: Int = 0
    var potionsCooked: Int = 0
    var piranhasKilled: Int = 0
    var nightHunt: Int = 0
    var ankhsUsed: Int = 0

    // Duration
    var duration: Float = 0f

    // Per-run
    var qualifiedForNoKilling: Boolean = true
    var completedWithNoKilling: Boolean = false

    fun reset() {
        goldCollected = 0
        deepestFloor = 0
        // ... reset all
    }
}
```

## Random Number Generation

The game uses `Random.kt` for deterministic generation:

```kotlin
object Random {
    private val generator = java.util.Random()

    fun Int(max: Int): Int = generator.nextInt(max)
    fun Int(min: Int, max: Int): Int = min + generator.nextInt(max - min)
    fun Float(max: Float): Float = generator.nextFloat() * max

    fun chances(chances: FloatArray): Int {
        val total = chances.sum()
        val roll = Float(total)
        var sum = 0f
        for (i in chances.indices) {
            sum += chances[i]
            if (roll < sum) return i
        }
        return chances.lastIndex
    }

    fun <T> element(array: Array<T>): T = array[Int(array.size)]
    fun <T> element(list: List<T>): T = list[Int(list.size)]

    fun shuffle(array: IntArray) {
        for (i in array.indices.reversed()) {
            val j = Int(i + 1)
            val temp = array[i]
            array[i] = array[j]
            array[j] = temp
        }
    }
}
```

## See Also

- [Architecture Overview](overview.md) - High-level architecture
- [Package Structure](package-structure.md) - Code organization
- [Actor System](../systems/actor-system.md) - Turn-based mechanics
- [Scene System](../systems/scene-system.md) - Scene management
