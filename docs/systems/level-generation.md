# Level Generation System

This document describes the procedural dungeon generation pipeline, including room shapes, interior features, corridor variety, passage features, and region-specific decorations.

## Generation Pipeline Overview

Every standard dungeon floor is a `RegularLevel`. The `build()` method drives the full pipeline:

```
RegularLevel.build()
  ├── initRooms()         — BSP splits the 32×32 grid into Room rects
  ├── Graph connectivity  — Entrance/exit placement, path building, random connections
  ├── assignRoomType()    — Labels rooms as STANDARD, TUNNEL, PASSAGE, or special
  ├── paint()             — Each room's painter fills terrain (StandardPainter, TunnelPainter, etc.)
  │     └── paintDoors()  — Door tiles placed at room boundaries
  ├── paintWater()        — Region-specific water overlay via Patch.generate()
  ├── paintGrass()        — Region-specific grass overlay
  ├── placeTraps()        — Random secret traps on EMPTY tiles
  └── decorate()          — Region subclass adds thematic decorations (SewerLevel, CityLevel, etc.)
```

**Path**: `levels/RegularLevel.kt`

### Room Splitting (BSP)

`initRooms()` recursively splits a 32×32 `Rect` into smaller rooms. Each split is constrained by `minRoomSize` (default 7) and `maxRoomSize` (default 9). The split terminates when a rect is small enough or random chance decides to stop. After splitting, all rooms discover their neighbours.

### Room Type Assignment

`assignRoomType()` labels each room. Dead-end rooms (1 connection) may become special rooms (Treasury, Library, etc.). Remaining rooms become `STANDARD` (at least 4) or `TUNNEL`. Some regions (Prison, City) convert all `TUNNEL` rooms to `PASSAGE` in their override.

---

## Room Shapes (StandardPainter)

**Path**: `levels/painters/StandardPainter.kt`

StandardPainter carves the interior of `STANDARD` rooms. Before applying any shape, walls are filled and doors set to `REGULAR` type.

### Tier 1: Dramatic Full-Room Decorations (20% chance)

On non-boss levels, there is a 20% chance (`Random.Int(5) == 0`) that a room gets a dramatic decoration instead of a normal shape. One of 6 types is chosen at random:

| Type | Min Size | Description |
|------|----------|-------------|
| Graveyard | 4×6 | Grass floor with tomb heaps in a line; one contains a random item |
| Burned | depth > 1 | Embers floor with scattered fire traps and inactive traps |
| Striped | 4 wide | Alternating `EMPTY_SP` and `HIGH_GRASS` rows or columns |
| Study | 6×6 | Bookshelf border, `EMPTY_SP` interior, pedestal at center |
| Bridge | 4×4, 2 doors | Water or chasm with `EMPTY_SP` paths between the two doors |
| Fissure | 5×5 | Empty border with `CHASM` eroded into the interior |

If the dramatic decoration cannot be applied (size/condition not met), the room falls through to normal shape selection.

### Shape Selection

Rooms that pass through Tier 1 are assigned a shape based on size:

| Shape | Constant | Weight (7×7+) | Weight (5×5–6×6) | Min Size |
|-------|----------|---------------|-------------------|----------|
| Rectangle | `SHAPE_RECTANGLE` | 40 | 65 | any |
| Rounded Rectangle | `SHAPE_ROUNDED_RECT` | 20 | 35 | 5×5 |
| Circle | `SHAPE_CIRCLE` | 15 | — | 7×7 |
| Diamond | `SHAPE_DIAMOND` | 12 | — | 7×7 |
| Cross | `SHAPE_CROSS` | 13 | — | 7×7 |

On boss levels or rooms smaller than 5×5, only `SHAPE_RECTANGLE` is used.

#### Shape Details

- **Rectangle** — Standard `Painter.fill(room, 1, EMPTY)`. The classic rectangular room.
- **Rounded Rectangle** — Starts with a full rectangle, then clips corners. For 7×7+ rooms, L-shaped 3-cell chunks are removed from each corner (octagonal look). For 5×5–6×6, single corner cells are clipped.
- **Circle** — Uses an ellipse equation `(dx/rx)² + (dy/ry)² ≤ 1` to carve an oval interior.
- **Diamond** — Uses a Manhattan-distance equation `|dx|/hw + |dy|/hh ≤ 1` to carve a diamond shape.
- **Cross** — Carves a 3-cell-wide horizontal bar and 3-cell-wide vertical bar through the room center.

#### Door Connectivity

Non-rectangular shapes may leave doors disconnected from the interior floor. `ensureDoorConnectivity()` walks inward from each door in the perpendicular direction, carving `EMPTY` tiles until it reaches existing floor.

### Tier 2: Interior Features (50% of rectangular rooms)

Rectangular rooms on non-boss levels have a 50% chance (`Random.Int(2) == 0`) of receiving one of 5 interior features:

| Feature | Min Size | Description |
|---------|----------|-------------|
| Pillars | 5×5 | `WALL` tiles on a 2-cell grid, skipping cells adjacent to doors |
| Corner Alcoves | 5×5 | 40% chance per corner to place a `WALL` tile, skipping door-adjacent corners |
| Center Feature | 5×5 | Single `PEDESTAL`, `STATUE`, or `EMPTY_SP` at center; 7×7+ rooms may get a 3×3 `WATER` or `EMBERS` pool instead |
| Wall Niches | 5×5 | 25% chance per inner-ring cell (excluding corners) to become `EMPTY_SP` |
| Scattered | any | 8% chance per interior cell to become a region-specific terrain tile |

#### Scattered Terrain by Region

| Depth Range | Region | Terrain |
|-------------|--------|---------|
| 1–5 | Sewers | `GRASS` |
| 6–10 | Prison | `EMPTY_DECO` |
| 11–15 | Caves | `EMBERS` |
| 16–20 | City | `EMPTY_DECO` |
| 21+ | Halls | `EMBERS` |

---

## Corridor Variety (TunnelPainter)

**Path**: `levels/painters/TunnelPainter.kt`

TunnelPainter creates narrow corridors connecting doors through the room center. It uses the level's `tunnelTile()` for the floor terrain.

### Base Corridor

The painter chooses horizontal or vertical orientation based on room dimensions (wider → horizontal, taller → vertical, equal → random). For each door, it draws a perpendicular line to the center row/column, then connects all endpoints along that center axis.

### Widened Tunnels

**Probability**: 33% (`Random.Int(3) == 0`), requires room height/width ≥ 5.

The center corridor is widened by 1 cell in a random perpendicular direction.

**Triple-wide**: An additional 50% chance (`Random.Int(2) == 0`) to widen by one more cell in the same direction, requiring height/width ≥ 7. This creates a 3-cell-wide corridor.

### Corridor Alcoves

**Probability**: 33% (`Random.Int(3) == 0`), independent of widening.

1–2 small niches are carved perpendicular to the corridor. For each floor tile, each adjacent wall cell has a 1-in-8 chance of being carved out. Alcoves stop after the target count (1–2) is reached.

### Tunnel Decorations

**Probability**: 20% (`Random.Int(5) == 0`).

1–3 floor tiles are replaced with `EMPTY_DECO`, chosen randomly from existing corridor cells with a 1-in-8 chance per cell.

---

## Passage Features (PassagePainter)

**Path**: `levels/painters/PassagePainter.kt`

PassagePainter creates a perimeter path along the room's inner edge, connecting all doors via the shortest arc around the room border. Used in Prison and City regions where all `TUNNEL` rooms are promoted to `PASSAGE`.

### Base Path

Doors are converted to perimeter coordinates, sorted, and the largest gap determines where the path does *not* go. The painter walks along the perimeter from the start joint to the end joint, setting each cell to the tunnel floor tile.

### Wider Path Segments

**Probability**: 33% (`Random.Int(3) == 0`), requires interior ≥ 3×3.

For each perimeter path cell, there is a 35% chance of carving 1 cell inward (toward the room center), converting a `WALL` to floor. This creates occasional wider spots along the corridor.

### Courtyard Fill

**Probability**: 25% (`Random.Int(4) == 0`), requires interior ≥ 3×3.

The room's inner rectangle (2 cells from each wall) is filled with `GRASS` or `WATER` (50/50). This creates an open courtyard surrounded by the perimeter path.

#### Courtyard Center Feature

If the courtyard is large enough (interior ≥ 5×5), there is a 33% chance of placing a center feature:

| Terrain | Description |
|---------|-------------|
| `PEDESTAL` | Stone pedestal |
| `STATUE` | Decorative statue |
| `EMPTY_DECO` | Decorated floor tile |

### Corner Features

At each of the 4 perimeter corners, there is a 20% chance (`Random.Int(5) == 0`) of replacing the floor tile with `EMPTY_DECO`.

---

## Region-Specific Decorations

Each region subclass overrides `decorate()` to add thematic features after painting. These run on `STANDARD` rooms with width and height > 3.

### Sewers (Floors 1–5)

**Path**: `levels/SewerLevel.kt`

| Feature | Condition | Description |
|---------|-----------|-------------|
| Drainage channel | width or height ≥ 6, 33% chance | A line of `WATER` through the room center (horizontal if wider, vertical if taller) |
| Mossy corners | 25% per corner | Each of the 4 interior corners may become `GRASS` |

**Global decorations**: Walls above water become `WALL_DECO` (dripping water particles). Empty tiles near multiple walls become `EMPTY_DECO` (mossy floor).

### Prison (Floors 6–10)

**Path**: `levels/PrisonLevel.kt`

| Feature | Condition | Description |
|---------|-----------|-------------|
| Cell bars | width ≥ 6, exactly 2 doors, 20% chance | `STATUE` tiles on every other cell across a middle row, with a gap at center for passage; skips door-adjacent cells |
| Blood stain corners | 30% per corner | Each of the 4 interior corners may become `EMPTY_DECO` |

**Global decorations**: Empty tiles near wall corners become `EMPTY_DECO` (blood stains). Walls above empty/special floor become `WALL_DECO` (torch with flame particles and halo).

### Caves (Floors 11–15)

**Path**: `levels/CavesLevel.kt`

| Feature | Condition | Description |
|---------|-----------|-------------|
| Cave-in corners | Random based on room area | Interior corner cells become `WALL` if bordered by walls on two sides, creating a natural eroded look |
| Cave rubble | width and height ≥ 5, 20% chance | 2–4 random interior cells become `EMBERS` (rubble debris) |

**Global decorations**: Empty tiles near walls become `EMPTY_DECO` (rough ground). Random walls become `WALL_DECO` (ore veins with sparkle particles). Chasms are placed between non-connected standard rooms on shared walls.

### City (Floors 16–20)

**Path**: `levels/CityLevel.kt`

| Feature | Condition | Description |
|---------|-----------|-------------|
| Carpet center | width and height ≥ 5, 33% chance | A 3×3 `EMPTY_SP` area at room center |
| Column pairs | width and height ≥ 6, 25% chance | `STATUE_SP` tiles at 4 symmetric interior positions (2 cells from each wall corner) |

**Global decorations**: Random empty tiles become `EMPTY_DECO` (10% chance). Random walls become `WALL_DECO` (smoke particles).

### Demon Halls (Floors 21–25)

**Path**: `levels/HallsLevel.kt`

| Feature | Condition | Description |
|---------|-----------|-------------|
| Lava pool | width and height ≥ 5, 25% chance | A 2×2 `WATER` pool (rendered as lava) at a random interior position |
| Skull pillars | width and height ≥ 6, 20% chance | Two `STATUE_SP` tiles flanking the center row, 2 cells from the left and right walls |

**Global decorations**: Empty tiles near passable neighbours become `EMPTY_DECO`. Walls become `WALL_DECO` (fire stream particles with additive blending). `minRoomSize` is raised to 6; `viewDistance` shrinks with depth.

---

## Terrain Type Reference

Terrain constants used by the decoration systems and their visual meaning:

| Constant | Value | Decorative Uses |
|----------|-------|-----------------|
| `EMPTY` | 1 | Default carved floor |
| `GRASS` | 2 | Sewer mossy corners, courtyard fill, scattered (Sewers) |
| `WATER` | 4 | Drainage channels (Sewers), courtyard fill, center feature pool, lava pools (Halls), bridge rooms |
| `WALL` | 5 | Pillars, corner alcoves, cave-in corners |
| `WALL_DECO` | 13 | Region wall effects: dripping water (Sewers), torches (Prison), ore veins (Caves), smoke (City), fire streams (Halls) |
| `PEDESTAL` | 12 | Center feature, courtyard center |
| `EMPTY_SP` | 15 | Wall niches, striped rooms, carpet center (City), bridge paths, study interior |
| `EMBERS` | 10 | Burned rooms, center feature pool, cave rubble, scattered (Caves/Halls) |
| `EMPTY_DECO` | 21 | Blood stains (Prison), rough ground (Caves), corner features (Passage), tunnel deco, scattered (Prison/City) |
| `STATUE` | 26 | Center feature, cell bars (Prison) |
| `STATUE_SP` | 27 | Column pairs (City), skull pillars (Halls) |
| `HIGH_GRASS` | 16 | Striped rooms |
| `CHASM` | 0 | Fissure rooms, bridge rooms, inter-room chasms (Caves) |
| `BOOKSHELF` | 28 | Study room border |

---

## Entity Placement and Collision Avoidance

During level generation, mobs, NPCs, and items are placed on cells. The system must prevent overlaps — an NPC should not spawn on top of an item heap, and items should not drop onto a cell occupied by a mob.

### Placement Methods

| Method | Location | Purpose |
|--------|----------|---------|
| `randomRespawnCell()` | `Level.kt` | Find a cell for a mob/NPC. Checks: passable, not visible, no character (`Actor.findChar`) |
| `randomRespawnCell()` | `RegularLevel.kt` (override) | Same but restricted to STANDARD rooms, 10-attempt limit |
| `randomDropCell()` | `RegularLevel.kt` | Find a cell for an item. Checks: passable, no character (`Actor.findChar`) |
| `drop(item, cell)` | `Level.kt` | Place an item heap at a cell. Special handling for alchemy pots and locked chests |

### Collision Checks Required

**NPC placement must check for heaps** — prevents NPCs from spawning on top of item piles:
```kotlin
// Correct pattern (from Imp.spawn):
do {
    npc.pos = level.randomRespawnCell()
} while (npc.pos == -1 || level.heaps[npc.pos] != null)
level.mobs.add(npc)
Actor.occupyCell(npc)
```

**Item placement must check for characters** — prevents items from dropping onto mob/NPC cells:
```kotlin
// randomDropCell checks:
if (passable[pos] && Actor.findChar(pos) == null) {
    return pos
}
```

### NPC Spawn Collision Summary

| NPC | Method | Checks Character? | Checks Heaps? |
|-----|--------|-------------------|---------------|
| Ghost | `randomRespawnCell()` + heap check | Yes (via `randomRespawnCell`) | Yes |
| Wandmaker | `room.random()` + terrain/character/heap check | Yes | Yes |
| Blacksmith | `room.random(1)` + heap check | No (dedicated room, no other mobs) | Yes |
| Imp | `randomRespawnCell()` + heap check | Yes (via `randomRespawnCell`) | Yes |
| AI NPC | `randomRespawnCell()` + heap check | Yes (via `randomRespawnCell`) | Yes |

### Key Invariant

After level generation completes, every cell should have **at most one** of:
- A character (mob or NPC) — tracked by `Actor.findChar(cell)`
- An item heap — tracked by `level.heaps[cell]`

Both can coexist at runtime (e.g., a mob walks onto a heap, or a mob drops loot on death), but during initial placement they should be kept separate to avoid visual overlap and interaction issues.

---

## See Also

- [Levels Catalog](../entities/levels.md) — All level types, room painters, and progression
- [Architecture Overview](../architecture/overview.md) — High-level system architecture
