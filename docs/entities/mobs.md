# Mobs Catalog

This document catalogs all enemies (mobs) in Amazing Pixel Dungeon.

## Overview

The game contains **45+ mob types** organized by dungeon region:
- Sewers (Floors 1-5): 6 mobs + 1 boss
- Prison (Floors 6-10): 6 mobs + 1 boss
- Caves (Floors 11-15): 5 mobs + 1 boss
- Metropolis (Floors 16-20): 6 mobs + 1 boss
- Demon Halls (Floors 21-25): 4 mobs + 1 boss
- Special mobs and NPCs

## Mob Base Class

**Path**: `actors/mobs/Mob.kt`

```kotlin
abstract class Mob : Char() {
    enum class State { SLEEPING, WANDERING, HUNTING, FLEEING, PASSIVE }

    var state: State = State.SLEEPING
    var enemy: Char? = null

    // Rewards
    var EXP: Int = 1
    var maxLvl: Int = 30  // Max level to gain XP
    var loot: Any? = null
    var lootChance: Float = 0f

    // AI methods
    abstract fun act(): Boolean
    protected open fun doSleep(): Boolean
    protected open fun doWander(): Boolean
    protected open fun doHunt(): Boolean
    protected open fun doFlee(): Boolean
}
```

---

## Sewers (Floors 1-5)

The sewers are the starting region with basic enemies.

### Regular Mobs

| Name | File | HP | Damage | EXP | Special |
|------|------|-----|--------|-----|---------|
| Marsupial Rat | `Rat.kt` | 8 | 1-5 | 1 | Basic enemy |
| Albino Rat | `Albino.kt` | 15 | 1-5 | 2 | Causes bleeding |
| Gnoll Scout | `Gnoll.kt` | 12 | 2-5 | 2 | - |
| Sewer Crab | `Crab.kt` | 15 | 1-7 | 3 | Armored (5 DR) |
| Sewer Fly | `Swarm.kt` | 1 | 1-4 | 3 | Splits when hit |
| Fetid Rat | `FetidRat.kt` | 15 | 2-5 | 4 | Quest mob, paralytic gas |

### Boss: Goo

**Path**: `actors/mobs/Goo.kt`

| Stat | Value |
|------|-------|
| HP | 80 |
| Damage | 10-24 |
| EXP | 10 |

**Abilities**:
- **Pump Up**: Charges for a devastating attack
- **Caustic Ooze**: Leaves damaging pools
- **Regeneration**: Heals in water

```kotlin
class Goo : Mob() {
    var pumpedUp = false

    override fun act(): Boolean {
        if (HP < HT && Random.Float() < 0.2f) {
            pumpedUp = true
            sprite?.showStatus(CharSprite.WARNING, "!!!")
        }
        return super.act()
    }

    override fun attack(enemy: Char): Boolean {
        val damage = if (pumpedUp) damageRoll() * 2 else damageRoll()
        pumpedUp = false
        return super.attack(enemy)
    }
}
```

---

## Prison (Floors 6-10)

The prison contains undead and guard enemies.

### Regular Mobs

| Name | File | HP | Damage | EXP | Special |
|------|------|-----|--------|-----|---------|
| Skeleton | `Skeleton.kt` | 25 | 3-8 | 5 | Explodes on death |
| Crazy Thief | `Thief.kt` | 20 | 1-7 | 5 | Steals and flees |
| Crazy Bandit | `Bandit.kt` | 20 | 1-7 | 5 | Upgraded thief |
| Gnoll Shaman | `Shaman.kt` | 18 | 2-6 | 6 | Ranged lightning |
| Necromancer | `Necromancer.kt` | 25 | 3-8 | 7 | Summons skeletons |
| Guard | `Guard.kt` | 40 | 4-12 | 6 | Chains target |

### Boss: Tengu

**Path**: `actors/mobs/Tengu.kt`

| Stat | Value |
|------|-------|
| HP | 120 |
| Damage | 6-12 |
| EXP | 20 |

**Abilities**:
- **Teleport**: Blinks around the arena
- **Shuriken**: Ranged attacks
- **Traps**: Spawns traps on low HP
- **Phase 2**: Becomes more aggressive at 50% HP

---

## Caves (Floors 11-15)

The caves feature dwarven and elemental enemies.

### Regular Mobs

| Name | File | HP | Damage | EXP | Special |
|------|------|-----|--------|-----|---------|
| Vampire Bat | `Bat.kt` | 30 | 5-15 | 7 | Life steal |
| Cave Spinner | `Spinner.kt` | 50 | 4-9 | 9 | Poisons, creates webs |
| Gnoll Brute | `Brute.kt` | 40 | 5-15 | 8 | Enrages at low HP |
| Shielded Brute | `Shielded.kt` | 40 | 5-15 | 8 | Blocks attacks |
| Fire Elemental | `Elemental.kt` | 65 | 6-12 | 10 | Immune to fire, burns |

### Boss: DM-300

**Path**: `actors/mobs/DM300.kt`

| Stat | Value |
|------|-------|
| HP | 200 |
| Damage | 20-36 |
| EXP | 30 |

**Abilities**:
- **Toxic Gas**: Emits poison cloud
- **Pylons**: Recharges from machinery
- **Crushing Blow**: High damage attack
- **Super Armor**: Reduced damage until pylons destroyed

---

## Metropolis (Floors 16-20)

The dwarven city has organized and magic-using enemies.

### Regular Mobs

| Name | File | HP | Damage | EXP | Special |
|------|------|-----|--------|-----|---------|
| Dwarf Monk | `Monk.kt` | 70 | 8-16 | 11 | Disarms weapons |
| Senior Monk | `Senior.kt` | 70 | 8-16 | 12 | Improved monk |
| Dwarf Warlock | `Warlock.kt` | 70 | 8-16 | 11 | Ranged shadow bolt |
| Golem | `Golem.kt` | 85 | 10-20 | 12 | High armor (12 DR) |
| Succubus | `Succubus.kt` | 80 | 10-15 | 12 | Charms, teleports |

### Boss: King of Dwarves

**Path**: `actors/mobs/King.kt`

| Stat | Value |
|------|-------|
| HP | 300 |
| Damage | 18-38 |
| EXP | 40 |

**Abilities**:
- **Summon Undead**: Spawns skeleton army
- **Crown of Thorns**: Reflects damage
- **Royal Decree**: Buffs minions
- **Undying**: Resurrection phase

---

## Demon Halls (Floors 21-25)

The final region with demonic enemies.

### Regular Mobs

| Name | File | HP | Damage | EXP | Special |
|------|------|-----|--------|-----|---------|
| Ripper Demon | `Ripper.kt` | 60 | 10-20 | 13 | Leaps, fast |
| Spawner | `Spawner.kt` | 120 | 10-20 | 14 | Summons rippers |
| Eye | `Eye.kt` | 100 | 20-30 | 13 | Death gaze beam |
| Scorpio | `Scorpio.kt` | 95 | 15-25 | 14 | Ranged, cripples |
| Acidic Scorpio | `Acidic.kt` | 95 | 15-25 | 15 | Improved scorpio |

### Boss: Yog-Dzewa

**Path**: `actors/mobs/Yog.kt`

| Stat | Value |
|------|-------|
| HP | 300 |
| Damage | - |
| EXP | 50 |

**Abilities**:
- **Fists**: Spawns Burning and Rotting Fists
- **Larvae**: Spawns endless larvae
- **Invulnerable**: Immune until fists die
- **Gaze**: Pulls heroes toward it

**Minions**:
| Name | HP | Damage | Ability |
|------|-----|--------|---------|
| Burning Fist | 200 | 20-32 | Fire attacks |
| Rotting Fist | 300 | 15-25 | Poison attacks |
| Larva | 25 | 15-20 | Swarm attack |

---

## Special Mobs

### Mimics

| Name | File | HP | Damage | EXP | Notes |
|------|------|-----|--------|-----|-------|
| Mimic | `Mimic.kt` | 50 | 10-20 | 5 | Disguised as chest |
| Golden Mimic | `GoldenMimic.kt` | 70 | 15-25 | 10 | Golden chest |

### Environmental

| Name | File | HP | Damage | EXP | Notes |
|------|------|-----|--------|-----|-------|
| Piranha | `Piranha.kt` | 10 | 10-15 | 0 | Water only |
| Statue | `Statue.kt` | 25+ | varies | 0 | Enchanted weapon |
| Wraith | `Wraith.kt` | 1 | 1-5 | 2 | Spawns from graves |

---

## NPCs

**Path**: `actors/mobs/npcs/`

Non-hostile characters that provide services or quests:

| NPC | File | Location | Service |
|-----|------|----------|---------|
| Ghost | `Ghost.kt` | Sewers | Quest: Kill Fetid Rat |
| Shopkeeper | `Shopkeeper.kt` | Every 5th floor | Buy/sell items |
| Wandmaker | `Wandmaker.kt` | Prison | Quest: Fetch item |
| Blacksmith | `Blacksmith.kt` | Caves | Quest: Reforge items |
| Imp | `Imp.kt` | Metropolis | Quest: Token collection |
| Rat King | `RatKing.kt` | Secret room | Easter egg |

### NPC Base

```kotlin
abstract class NPC : Mob() {
    init {
        HP = 1
        HT = 1
        state = State.PASSIVE
    }

    override fun damage(dmg: Int, src: Any) {
        // NPCs don't take damage
    }

    abstract fun interact(): Boolean
}
```

### NPC Spawn Pattern

All NPCs must avoid spawning on cells that already contain item heaps or other characters. The correct pattern combines `randomRespawnCell()` (which checks for characters) with an explicit heap check:

```kotlin
// Standard NPC spawn pattern
do {
    npc.pos = level.randomRespawnCell()
} while (npc.pos == -1 || level.heaps[npc.pos] != null)
level.mobs.add(npc)
Actor.occupyCell(npc)
```

For NPCs placed in specific rooms (e.g., Wandmaker), check terrain, characters, and heaps:
```kotlin
do {
    npc.pos = room.random()
} while (level.map[npc.pos] == Terrain.ENTRANCE
    || level.map[npc.pos] == Terrain.SIGN
    || Actor.findChar(npc.pos) != null
    || level.heaps[npc.pos] != null)
```

See [Level Generation — Entity Placement](../systems/level-generation.md#entity-placement-and-collision-avoidance) for the full collision avoidance reference.

---

## Mob AI States

```
┌─────────────┐
│  SLEEPING   │◄─────────────────────────────┐
└──────┬──────┘                              │
       │ notice hero                         │
       ▼                                     │
┌─────────────┐     lost target     ┌───────┴─────┐
│   HUNTING   │────────────────────►│  WANDERING  │
└──────┬──────┘                     └─────────────┘
       │ HP < 25%
       ▼
┌─────────────┐
│   FLEEING   │
└─────────────┘
```

### State Behaviors

| State | Behavior |
|-------|----------|
| SLEEPING | Idle, wakes on noise/visibility |
| WANDERING | Random movement, watches for hero |
| HUNTING | Pursues and attacks hero |
| FLEEING | Moves away from hero |
| PASSIVE | NPCs only, ignores hero |

---

## Mob Generation

The `Bestiary.kt` class handles mob spawning:

```kotlin
object Bestiary {
    fun mob(depth: Int): Mob {
        return when (depth) {
            in 1..4 -> sewers()
            5 -> Goo()
            in 6..9 -> prison()
            10 -> Tengu()
            in 11..14 -> caves()
            15 -> DM300()
            in 16..19 -> city()
            20 -> King()
            in 21..24 -> halls()
            25 -> Yog()
            else -> Rat()  // Fallback
        }
    }

    private fun sewers(): Mob {
        return when (Random.chances(floatArrayOf(3f, 1f, 2f, 1f))) {
            0 -> Rat()
            1 -> Albino()
            2 -> Gnoll()
            3 -> Crab()
            else -> Rat()
        }
    }
}
```

## See Also

- [Actor System](../systems/actor-system.md) - AI and combat
- [Buffs](buffs.md) - Status effects
- [Levels](levels.md) - Dungeon generation
