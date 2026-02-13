# Levels Catalog

This document catalogs all level types in Amazing Pixel Dungeon.

## Overview

The game has **26 dungeon floors** across 5 regions, with **48+ level-related classes** including:
- Base level classes
- Region-specific levels
- Boss arenas
- Special levels
- Room painters (28 types)
- Traps (9 types)

## Level Structure

### Level.kt
**Path**: `levels/Level.kt`

Base class for all dungeon floors:

```kotlin
abstract class Level : Bundlable {
    companion object {
        const val WIDTH = 32
        const val HEIGHT = 32
        const val LENGTH = WIDTH * HEIGHT  // 1024 cells
    }

    // Terrain map
    var map: IntArray = IntArray(LENGTH)

    // Visibility
    var visited: BooleanArray = BooleanArray(LENGTH)
    var mapped: BooleanArray = BooleanArray(LENGTH)

    // Special locations
    var entrance: Int = -1
    var exit: Int = -1

    // Contents
    val mobs = HashSet<Mob>()
    val heaps = SparseArray<Heap>()
    val blobs = HashMap<Class<out Blob>, Blob>()
    val plants = SparseArray<Plant>()
    val traps = SparseArray<Trap>()

    // Abstract methods
    abstract fun tilesTex(): String
    abstract fun create()
    open fun createMobs() { }
    open fun createItems() { }

    // Pathfinding
    fun passable(): BooleanArray
    fun adjacent(a: Int, b: Int): Boolean
    fun distance(a: Int, b: Int): Int
}
```

### Terrain Constants

**Path**: `levels/Terrain.kt`

```kotlin
object Terrain {
    const val CHASM = 0
    const val EMPTY = 1
    const val GRASS = 2
    const val EMPTY_WELL = 3
    const val WATER = 4
    const val WALL = 5
    const val DOOR = 6
    const val OPEN_DOOR = 7
    const val ENTRANCE = 8
    const val EXIT = 9
    const val EMBERS = 10
    const val LOCKED_DOOR = 11
    const val PEDESTAL = 12
    const val WALL_DECO = 13
    const val BARRICADE = 14
    const val EMPTY_SP = 15
    const val HIGH_GRASS = 16
    const val SECRET_DOOR = 17
    const val SECRET_TRAP = 18
    const val TRAP = 19
    const val INACTIVE_TRAP = 20
    const val EMPTY_DECO = 21
    const val LOCKED_EXIT = 22
    const val UNLOCKED_EXIT = 23
    const val SIGN = 24
    const val WELL = 25
    const val STATUE = 26
    const val STATUE_SP = 27
    const val BOOKSHELF = 28
    const val ALCHEMY = 29
    const val CRAFTING_TABLE = 64  // SOLID — interact from adjacent
    const val FURNACE = 65         // SOLID — interact from adjacent
}
```

---

## Village (Floor 0)

### VillageLevel
**Path**: `levels/VillageLevel.kt`

Outdoor safe hub where the player starts. Hand-crafted layout (not procedurally generated). The player can return by ascending from depth 1.

| Property | Value |
|----------|-------|
| Tileset | `tiles_village.png` |
| Water | Pond (SE corner) |
| Feeling | Always NONE |
| Respawner | None (safe zone) |

**Layout**: Three shops (weapon NW, potion NE, tavern SW), central square with healing well, signpost, and campfire, village garden (E), herbalist's alchemy pot (W), hidden stash behind weapon shop, workshop with crafting table and furnace (SE).

**NPCs**: 3 Shopkeepers, VillageElder, 1-2 Rats (outskirts).

**Atmospheric effects**: Wind particles on grass, leaf particles on hedges, smoke above campfire, pond sparkles, Foliage light shafts in garden.

**Discoverable content**:
- Village Garden — Foliage blob (golden light, Shadows buff), Sungrass + Brightcap plants
- Herbalist's Corner — functional Alchemy pot for brewing potions from seeds
- Hidden Stash — SECRET_DOOR behind weapon shop leads to chest (Honeypot/Ankh/HolyWater/SmokeBomb)
- Workshop — Crafting Table (all non-furnace recipes) and Furnace (smelting ores, cooking meat, firing stone). Both are SOLID tiles; player interacts from an adjacent cell via `HeroAction.UseStation`. MaterialBag available for purchase.

---

## Region 1: Sewers (Floors 1-5)

### SewerLevel
**Path**: `levels/SewerLevel.kt`

Standard sewer floor generation.

| Property | Value |
|----------|-------|
| Tileset | `tiles0.png` |
| Water | Common |
| Grass | Rare |
| Feeling | Random (dark, monsters, traps) |

```kotlin
class SewerLevel : RegularLevel() {
    override fun tilesTex(): String = Assets.TILES_SEWERS
    override fun waterTex(): String = Assets.WATER_SEWERS

    override fun createMobs() {
        val nMobs = if (Dungeon.depth == 1) 1 else 2 + Dungeon.depth
        for (i in 0 until nMobs) {
            mobs.add(Bestiary.mob(Dungeon.depth).apply {
                pos = randomRespawnCell()
            })
        }
    }
}
```

### SewerBossLevel
**Path**: `levels/SewerBossLevel.kt`

Goo boss arena (Floor 5).

| Property | Value |
|----------|-------|
| Layout | Fixed arena |
| Boss | Goo |
| Water | Flooded center |
| Exit | Locked until boss dies |

---

## Region 2: Prison (Floors 6-10)

### PrisonLevel
**Path**: `levels/PrisonLevel.kt`

Prison floors with cells and corridors.

| Property | Value |
|----------|-------|
| Tileset | `tiles1.png` |
| Water | Moderate |
| Grass | Moderate |
| Special | Prison cells, torture rooms |

### PrisonBossLevel
**Path**: `levels/PrisonBossLevel.kt`

Tengu boss arena (Floor 10).

| Property | Value |
|----------|-------|
| Layout | Multi-phase arena |
| Boss | Tengu |
| Traps | Spawns during fight |
| Mechanics | Maze walls |

---

## Region 3: Caves (Floors 11-15)

### CavesLevel
**Path**: `levels/CavesLevel.kt`

Natural cave system with mining theme.

| Property | Value |
|----------|-------|
| Tileset | `tiles2.png` |
| Water | Common (underground pools) |
| Chasms | Common |
| Special | Mine carts, ore veins |

### CavesBossLevel
**Path**: `levels/CavesBossLevel.kt`

DM-300 boss arena (Floor 15).

| Property | Value |
|----------|-------|
| Layout | Factory/machinery |
| Boss | DM-300 |
| Pylons | Must destroy to damage boss |
| Hazards | Toxic gas, traps |

---

## Region 4: Metropolis (Floors 16-20)

### CityLevel
**Path**: `levels/CityLevel.kt`

Dwarven city with buildings and streets.

| Property | Value |
|----------|-------|
| Tileset | `tiles3.png` |
| Water | Rare |
| Grass | None |
| Special | Libraries, statues |

### CityBossLevel
**Path**: `levels/CityBossLevel.kt`

Dwarf King arena (Floor 20).

| Property | Value |
|----------|-------|
| Layout | Throne room |
| Boss | King of Dwarves |
| Summons | Undead army |
| Pedestals | Used in fight mechanics |

---

## Region 5: Demon Halls (Floors 21-25)

### LastShopLevel
**Path**: `levels/LastShopLevel.kt`

Final shop before demon halls (Floor 21).

| Property | Value |
|----------|-------|
| Layout | Small safe area |
| Shop | Imp shopkeeper |
| Features | Final supplies |

### HallsLevel
**Path**: `levels/HallsLevel.kt`

Hellish demon realm.

| Property | Value |
|----------|-------|
| Tileset | `tiles4.png` |
| Water | Lava pools |
| Grass | None |
| Special | Demonic summoning circles |

### HallsBossLevel
**Path**: `levels/HallsBossLevel.kt`

Yog-Dzewa arena (Floor 25).

| Property | Value |
|----------|-------|
| Layout | Circular pit |
| Boss | Yog-Dzewa |
| Mechanics | Fist phases |
| Hazards | Endless larvae |

---

## Special Levels

### LastLevel
**Path**: `levels/LastLevel.kt`

Amulet chamber (Floor 26).

| Property | Value |
|----------|-------|
| Layout | Single room |
| Contents | Amulet of Yendor |
| Exit | None (must ascend) |

### DeadEndLevel
**Path**: `levels/DeadEndLevel.kt`

Blocked passage (beyond Floor 26).

| Property | Value |
|----------|-------|
| Layout | Empty corridor |
| Purpose | Prevents further descent |

---

## Level Generation

> **Deep dive**: See [Level Generation System](../systems/level-generation.md) for the full pipeline, room shapes, interior features, corridor variety, and region-specific decorations.

### RegularLevel
**Path**: `levels/RegularLevel.kt`

Procedural generation for standard floors:

```kotlin
abstract class RegularLevel : Level() {
    val rooms = ArrayList<Room>()

    override fun create() {
        // 1. Generate rooms
        if (!initRooms()) {
            return create()  // Retry
        }

        // 2. Connect rooms
        for (room in rooms) {
            connectRoom(room)
        }

        // 3. Paint rooms
        for (room in rooms) {
            room.paint(this)
        }

        // 4. Place entrance/exit
        setupEntrance()
        setupExit()

        // 5. Place special features
        createMobs()
        createItems()
        placeTraps()
    }

    protected open fun initRooms(): Boolean {
        rooms.clear()

        // Create special rooms first
        rooms.add(Room().apply { type = Room.Type.ENTRANCE })
        rooms.add(Room().apply { type = Room.Type.EXIT })

        // Add random room types
        val nRooms = 7 + Random.Int(4)
        for (i in 0 until nRooms) {
            rooms.add(Room())
        }

        // Place rooms using random placement
        return placeRooms()
    }
}
```

### Room Class
**Path**: `levels/Room.kt`

```kotlin
class Room : Rect() {
    enum class Type {
        EMPTY, STANDARD, ENTRANCE, EXIT, BOSS_EXIT,
        TUNNEL, PASSAGE, SHOP, BLACKSMITH,
        TREASURY, ARMORY, LIBRARY, LABORATORY,
        VAULT, GARDEN, CRYPT, STATUE, POOL,
        RAT_KING, WEAK_FLOOR, MAGIC_WELL, ALTAR
    }

    var type: Type = Type.EMPTY
    val connected = HashMap<Room, Door>()
    val neighbors = HashSet<Room>()

    fun paint(level: Level) {
        Painter.get(type).paint(level, this)
    }
}
```

---

## Room Painters

**Path**: `levels/painters/`

Room painters decorate rooms with specific themes:

| Painter | File | Description |
|---------|------|-------------|
| StandardPainter | `StandardPainter.kt` | Room shapes (rectangle, rounded rect, circle, diamond, cross), dramatic decorations, and interior features (pillars, alcoves, center features, wall niches, scattered) |
| EntrancePainter | `EntrancePainter.kt` | Stairs up |
| ExitPainter | `ExitPainter.kt` | Stairs down |
| BossExitPainter | `BossExitPainter.kt` | Locked boss exit |
| ShopPainter | `ShopPainter.kt` | Item shop layout |
| TreasuryPainter | `TreasuryPainter.kt` | Gold and items |
| ArmoryPainter | `ArmoryPainter.kt` | Weapon racks |
| LibraryPainter | `LibraryPainter.kt` | Bookshelves |
| LaboratoryPainter | `LaboratoryPainter.kt` | Alchemy stations |
| VaultPainter | `VaultPainter.kt` | Locked treasure |
| GardenPainter | `GardenPainter.kt` | Plants and seeds |
| CryptPainter | `CryptPainter.kt` | Tombs and undead |
| StatuePainter | `StatuePainter.kt` | Animated statues |
| PoolPainter | `PoolPainter.kt` | Water features |
| MagicWellPainter | `MagicWellPainter.kt` | Magic well |
| AltarPainter | `AltarPainter.kt` | Sacrificial altar |
| TunnelPainter | `TunnelPainter.kt` | Narrow passage with optional widening (2- or 3-wide corridors), alcoves, and decorated floor tiles |
| PassagePainter | `PassagePainter.kt` | Perimeter path with wider segments, optional courtyard fill (grass/water), center features, and corner decorations |
| TrapsPainter | `TrapsPainter.kt` | Trap-filled room |
| WeakFloorPainter | `WeakFloorPainter.kt` | Collapsing floor |
| RatKingPainter | `RatKingPainter.kt` | Secret throne |
| BlacksmithPainter | `BlacksmithPainter.kt` | Forge room |
| WorkshopPainter | `WorkshopPainter.kt` | Crafting table + furnace room |

---

## Traps

**Path**: `levels/traps/`

| Trap | File | Effect |
|------|------|--------|
| Fire Trap | `FireTrap.kt` | Burns target |
| Poison Trap | `PoisonTrap.kt` | Poisons target |
| Toxic Trap | `ToxicTrap.kt` | Poison gas cloud |
| Paralytic Trap | `ParalyticTrap.kt` | Paralyzes target |
| Lightning Trap | `LightningTrap.kt` | Electric damage |
| Gripping Trap | `GrippingTrap.kt` | Roots and damages |
| Summoning Trap | `SummoningTrap.kt` | Spawns enemies |
| Alarm Trap | `AlarmTrap.kt` | Alerts all mobs |
| Teleport Trap | `TeleportTrap.kt` | Random teleport |

### Trap Mechanics

```kotlin
abstract class Trap {
    var pos: Int = -1
    var visible: Boolean = false
    var active: Boolean = true

    fun trigger(ch: Char?) {
        if (active) {
            activate(ch)
            active = false
            Dungeon.level!!.map[pos] = Terrain.INACTIVE_TRAP
        }
    }

    abstract fun activate(ch: Char?)
}
```

---

## Level Features

**Path**: `levels/features/`

| Feature | File | Description |
|---------|------|-------------|
| Chasm | `Chasm.kt` | Fall damage, level skip |
| Door | `Door.kt` | Openable doors |
| High Grass | `HighGrass.kt` | Hides items, seeds |
| Sign | `Sign.kt` | Readable lore |
| Alchemy Pot | `AlchemyPot.kt` | Brew potions |
| Harvestable Wall | `HarvestableWall.kt` | Mine ore/stone from walls |

---

## Level Feelings

Random modifiers applied to levels:

| Feeling | Effect |
|---------|--------|
| NONE | Normal level |
| DARK | Reduced vision |
| LARGE | More rooms |
| TRAPS | Extra traps |
| SECRETS | Hidden doors |
| MONSTERS | More enemies |

```kotlin
enum class Feeling {
    NONE, DARK, LARGE, TRAPS, SECRETS, MONSTERS
}

fun assignFeeling() {
    feeling = when {
        Dungeon.depth <= 1 -> Feeling.NONE
        Random.Float() < 0.15f -> Random.oneOf(
            Feeling.DARK, Feeling.LARGE,
            Feeling.TRAPS, Feeling.SECRETS
        )
        else -> Feeling.NONE
    }
}
```

---

## Level Progression

```
Floor 0:     VillageLevel (safe hub)
Floor 1-4:   SewerLevel
Floor 5:     SewerBossLevel (Goo)
Floor 6-9:   PrisonLevel
Floor 10:    PrisonBossLevel (Tengu)
Floor 11-14: CavesLevel
Floor 15:    CavesBossLevel (DM-300)
Floor 16-19: CityLevel
Floor 20:    CityBossLevel (King)
Floor 21:    LastShopLevel
Floor 22-24: HallsLevel
Floor 25:    HallsBossLevel (Yog)
Floor 26:    LastLevel (Amulet)
```

## See Also

- [Level Generation System](../systems/level-generation.md) - Pipeline, room shapes, decorations
- [Mobs](mobs.md) - Enemies per region
- [Actor System](../systems/actor-system.md) - Game entities
- [Scene System](../systems/scene-system.md) - Level transitions
