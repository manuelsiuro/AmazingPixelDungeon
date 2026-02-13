# Plants Catalog

This document catalogs all plant types in Amazing Pixel Dungeon.

## Overview

The game contains **9 plant types**, each with unique effects when trampled or harvested.

## Plant Base Class

**Path**: `plants/Plant.kt`

```kotlin
abstract class Plant : Bundlable {
    var pos: Int = -1
    var image: Int = 0

    abstract fun activate(): Unit

    fun wither() {
        Dungeon.level!!.uproot(pos)
        sprite?.kill()

        if (visible) {
            CellEmitter.get(pos).burst(LeafParticle.GENERAL, 6)
        }
    }

    // Seeds are nested classes
    abstract class Seed : Item() {
        abstract val plantClass: Class<out Plant>

        fun couch(pos: Int): Plant {
            return plantClass.newInstance().apply {
                this.pos = pos
                Dungeon.level!!.plant(this, pos)
            }
        }
    }
}
```

---

## Plant Types

### Firebloom

**Path**: `plants/Firebloom.kt`

| Property | Value |
|----------|-------|
| Image | Red flower |
| Effect | Creates fire explosion |
| Seed | Firebloom Seed |
| Potion | Potion of Liquid Flame |

```kotlin
class Firebloom : Plant() {
    init {
        image = 0
    }

    override fun activate() {
        GameScene.add(Blob.seed(pos, 2, Fire::class.java))

        if (Dungeon.visible[pos]) {
            Sample.INSTANCE.play(Assets.SND_BURNING)
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Firebloom::class.java
        override val alchemyClass = PotionOfLiquidFlame::class.java
    }
}
```

**Uses**:
- Combat: Damage enemies
- Utility: Light torches, burn obstacles
- Alchemy: Create fire potions

---

### Icecap

**Path**: `plants/Icecap.kt`

| Property | Value |
|----------|-------|
| Image | Blue flower |
| Effect | Freezes nearby area |
| Seed | Icecap Seed |
| Potion | Potion of Frost |

```kotlin
class Icecap : Plant() {
    init {
        image = 1
    }

    override fun activate() {
        // Freeze area around plant
        PathFinder.NEIGHBOURS9.forEach { offset ->
            val cell = pos + offset
            Dungeon.level!!.charAt(cell)?.let { ch ->
                Buff.affect(ch, Frost::class.java, Frost.duration(ch) * 2)
            }
        }

        // Freeze fire
        Fire.freeze(pos)

        CellEmitter.get(pos).start(SnowParticle.FACTORY, 0.2f, 6)
    }

    class Seed : Plant.Seed() {
        override val plantClass = Icecap::class.java
        override val alchemyClass = PotionOfFrost::class.java
    }
}
```

**Uses**:
- Combat: Freeze and slow enemies
- Utility: Extinguish fires, freeze water
- Alchemy: Create frost potions

---

### Sungrass

**Path**: `plants/Sungrass.kt`

| Property | Value |
|----------|-------|
| Image | Yellow flower |
| Effect | Heals over time |
| Seed | Sungrass Seed |
| Potion | Potion of Healing |

```kotlin
class Sungrass : Plant() {
    init {
        image = 2
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            Buff.affect(ch, Health::class.java).boost(ch.HT)
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        }
    }

    class Health : Buff() {
        private var healAmount: Int = 0
        private var partialHeal: Float = 0f

        fun boost(amount: Int) {
            healAmount = max(healAmount, amount)
        }

        override fun act(): Boolean {
            // Heal over time
            partialHeal += healAmount / 50f
            if (partialHeal >= 1) {
                val heal = partialHeal.toInt()
                target!!.HP = min(target!!.HT, target!!.HP + heal)
                partialHeal -= heal
            }

            healAmount--
            if (healAmount <= 0) {
                detach()
            }

            spend(TICK)
            return true
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Sungrass::class.java
        override val alchemyClass = PotionOfHealing::class.java
    }
}
```

**Uses**:
- Combat: Emergency healing
- Exploration: Recover between fights
- Alchemy: Create healing potions

---

### Earthroot

**Path**: `plants/Earthroot.kt`

| Property | Value |
|----------|-------|
| Image | Brown root |
| Effect | Grants armor buff |
| Seed | Earthroot Seed |
| Potion | Potion of Barkskin |

```kotlin
class Earthroot : Plant() {
    init {
        image = 3
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            if (ch is Hero) {
                Buff.affect(ch, Armor::class.java).level(ch.HT)
                GLog.p("Your skin " + "hardens like bark!")
            }
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.bottom(pos).start(EarthParticle.FACTORY, 0.05f, 8)
        }
    }

    class Armor : Buff() {
        var level: Int = 0

        fun level(value: Int) {
            level = max(level, value)
        }

        fun absorb(damage: Int): Int {
            if (damage >= level) {
                detach()
                return damage - level
            }
            level -= damage
            return 0
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Earthroot::class.java
        override val alchemyClass = PotionOfBarkskin::class.java
    }
}
```

**Uses**:
- Combat: Damage absorption
- Boss fights: Tank heavy hits
- Alchemy: Create defense potions

---

### Fadeleaf

**Path**: `plants/Fadeleaf.kt`

| Property | Value |
|----------|-------|
| Image | Pale leaf |
| Effect | Teleports trampler |
| Seed | Fadeleaf Seed |
| Potion | Potion of Invisibility |

```kotlin
class Fadeleaf : Plant() {
    init {
        image = 4
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            if (ch is Hero) {
                ScrollOfTeleportation.teleportHero(ch)
            } else {
                // Random teleport for mobs
                val newPos = Dungeon.level!!.randomRespawnCell()
                if (newPos != -1) {
                    ch.pos = newPos
                    ch.sprite?.place(newPos)
                }
            }
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Fadeleaf::class.java
        override val alchemyClass = PotionOfInvisibility::class.java
    }
}
```

**Uses**:
- Escape: Emergency teleport
- Combat: Separate enemies
- Alchemy: Create invisibility potions

---

### Dreamweed

**Path**: `plants/Dreamweed.kt`

| Property | Value |
|----------|-------|
| Image | Purple flower |
| Effect | Causes sleep/confusion |
| Seed | Dreamweed Seed |
| Potion | Potion of Purity |

```kotlin
class Dreamweed : Plant() {
    init {
        image = 5
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            if (ch is Mob) {
                Buff.affect(ch, Sleep::class.java)
            } else {
                // Confused movement for hero
                Buff.affect(ch, Vertigo::class.java, 5f)
            }
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(Speck.factory(Speck.WOOL), 6)
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Dreamweed::class.java
        override val alchemyClass = PotionOfPurity::class.java
    }
}
```

**Uses**:
- Combat: Disable enemies
- Stealth: Put guards to sleep
- Alchemy: Create purity potions

---

### Sorrowmoss

**Path**: `plants/Sorrowmoss.kt`

| Property | Value |
|----------|-------|
| Image | Dark green |
| Effect | Poisons trampler |
| Seed | Sorrowmoss Seed |
| Potion | Potion of Toxic Gas |

```kotlin
class Sorrowmoss : Plant() {
    init {
        image = 6
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            Buff.affect(ch, Poison::class.java).set(Poison.duration(ch) * 2)
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3)
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Sorrowmoss::class.java
        override val alchemyClass = PotionOfToxicGas::class.java
    }
}
```

**Uses**:
- Traps: Poison approaching enemies
- Combat: DoT damage
- Alchemy: Create poison potions

---

### Stormvine

**Path**: `plants/Stormvine.kt`

| Property | Value |
|----------|-------|
| Image | Crackling vine |
| Effect | Causes vertigo |
| Seed | Stormvine Seed |
| Potion | Potion of Levitation |

```kotlin
class Stormvine : Plant() {
    init {
        image = 7
    }

    override fun activate() {
        Dungeon.level!!.charAt(pos)?.let { ch ->
            Buff.affect(ch, Vertigo::class.java, Vertigo.DURATION)
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).burst(SparkParticle.FACTORY, 10)
        }
    }

    class Seed : Plant.Seed() {
        override val plantClass = Stormvine::class.java
        override val alchemyClass = PotionOfLevitation::class.java
    }
}
```

**Uses**:
- Combat: Disorient enemies
- Alchemy: Create levitation potions

---

### Rotberry

**Path**: `plants/Rotberry.kt`

| Property | Value |
|----------|-------|
| Image | Rotten berry |
| Effect | Quest item |
| Seed | Rotberry Seed |
| Potion | Potion of Strength |

```kotlin
class Rotberry : Plant() {
    init {
        image = 8
    }

    override fun activate() {
        // Special quest plant - doesn't have normal effect
        Dungeon.level!!.drop(Seed(), pos)
    }

    class Seed : Plant.Seed() {
        override val plantClass = Rotberry::class.java
        override val alchemyClass = PotionOfStrength::class.java

        override fun desc(): String {
            return "This is a very pungent seed that can only be found " +
                   "in the Rotberry plant. The Wandmaker will need it."
        }
    }
}
```

**Uses**:
- Quest: Trade with Wandmaker
- Alchemy: Create strength potions

---

## Plant Mechanics

### Trampling

Plants activate when any character steps on them:

```kotlin
// In Level.kt
fun press(cell: Int, ch: Char?) {
    plants[cell]?.let { plant ->
        plant.activate()
        plant.wither()
    }
}
```

### Planting Seeds

Seeds can be thrown to plant them:

```kotlin
// In Seed.kt
override fun onThrow(cell: Int) {
    if (Dungeon.level!!.map[cell] == Terrain.EMPTY ||
        Dungeon.level!!.map[cell] == Terrain.EMBERS ||
        Dungeon.level!!.map[cell] == Terrain.GRASS ||
        Dungeon.level!!.map[cell] == Terrain.HIGH_GRASS) {

        Dungeon.level!!.plant(couch(cell), cell)
    } else {
        super.onThrow(cell)
    }
}
```

### Alchemy

Seeds can be brewed into potions:

```kotlin
// At Alchemy Pot
fun brew(seed: Plant.Seed): Potion {
    return seed.alchemyClass.newInstance()
}
```

---

## Plant Sources

Plants can be found:

| Source | Notes |
|--------|-------|
| High Grass | Random chance when searching |
| Garden Rooms | Pre-placed plants |
| Wand of Regrowth | Creates grass and plants |
| Seed Drops | From any plant |
| Shop | Seeds for sale |

### Herbalism Bonus

Ring of Herbalism increases seed drops:

```kotlin
fun dropSeed(): Boolean {
    val herbalism = Dungeon.hero!!.buff(RingOfHerbalism.Herbalism::class.java)
    val bonus = herbalism?.level ?: 0
    return Random.Float() < 0.1f * (1 + bonus)
}
```

---

## Strategic Uses

### Combat Combinations

| Combination | Effect |
|-------------|--------|
| Firebloom + Icecap | Cancel each other |
| Earthroot before boss | Damage absorption |
| Dreamweed + ranged | Sleep then attack |
| Fadeleaf in danger | Emergency escape |

### Room Setups

| Setup | Strategy |
|-------|----------|
| Garden defense | Plant Firebloom at entrance |
| Healing station | Sungrass + safe area |
| Escape route | Fadeleaf at choke point |

---

---

## Farming Crops

**Path**: `farming/`

A separate crop system for growing food. Unlike dungeon plants (which activate on trample), crops grow over turns on farmland terrain and are harvested for produce.

### Crop Types

| Crop | File | Growth (turns) | Hydrated | Yield | Produce |
|------|------|---------------|----------|-------|---------|
| Wheat | `WheatSeed.kt` | 40 | ~27 | 1-2 | Wheat → Bread (furnace) |
| Carrot | `CarrotSeed.kt` | 30 | ~20 | 1-3 | Carrot (edible raw) |
| Potato | `PotatoSeed.kt` | 30 | ~20 | 1-3 | Potato → Baked Potato (furnace) |
| Melon | `MelonSeed.kt` | 60 | ~40 | 3-5 | Melon Slice (edible raw) |

### Growth Stages

Crops progress through 4 visual stages, each with its own sprite frame:

| Stage | Name | Trigger (% of growth time) |
|-------|------|---------------------------|
| 0 | Seedling | 0-25% |
| 1 | Sprout | 25-55% |
| 2 | Vegetative | 55-100% |
| 3 | Mature | 100% (ready to harvest) |

### Farming Mechanics

**Terrain**:
- `Terrain.FARMLAND` (68) — tilled earth, created by using a Hoe on grass/empty ground
- `Terrain.HYDRATED_FARMLAND` (69) — moist farmland near water, crops grow 1.5x faster
- Use Water Bucket on farmland to hydrate it directly
- Empty farmland decays back to grass after 200 turns

**Planting**: Use a crop seed → select adjacent farmland cell → seed is consumed, crop sprite appears

**Growth**: `CropData.updateStage(currentTime)` calculates elapsed time × hydration multiplier. `CropSprite` polls crop data every 0.5s to update its visual frame.

**Harvesting**: When stage ≥ 3, use Hoe or interact to harvest. Yields produce + 1-2 seeds back.

**Bonemeal**: Instantly matures a crop (sets `plantedAt` far in the past). Calls `GameScene.updateCrop()` to refresh the sprite immediately.

**Planter Box**: Portable farming — plant a seed inside, it grows as you explore. No farmland terrain needed.

### Key Classes

| Class | File | Purpose |
|-------|------|---------|
| `CropType` | `farming/CropType.kt` | Enum: growth time, yield range |
| `CropData` | `farming/CropData.kt` | Per-cell crop state (pos, type, plantedAt, stage, hydrated) |
| `CropManager` | `farming/CropManager.kt` | Plant/harvest/update logic, time tracking |
| `CropSeed` | `farming/CropSeed.kt` | Abstract seed item with PLANT action |
| `CropSprite` | `sprites/CropSprite.kt` | Visual sprite with stage-based frames |
| `Farmland` | `levels/features/Farmland.kt` | Farmland terrain interaction |
| `PlanterBox` | `items/food/farming/PlanterBox.kt` | Portable crop container |

---

## See Also

- [Items](items.md) - Seeds, potions, and farming tools
- [Buffs](buffs.md) - Plant-applied effects
- [Levels](levels.md) - Garden rooms, farmland terrain
