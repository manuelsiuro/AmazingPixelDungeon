# Buffs Catalog

This document catalogs all buffs and debuffs in Amazing Pixel Dungeon.

## Overview

The game contains **41 buff types** organized into:
- Positive buffs (enhancements)
- Negative buffs (debuffs/status effects)
- Special buffs (mechanics)

## Buff Base Class

**Path**: `actors/buffs/Buff.kt`

```kotlin
abstract class Buff : Actor() {
    var target: Char? = null

    companion object {
        const val NONE = -1

        fun <T : Buff> affect(char: Char, buffClass: Class<T>): T
        fun <T : Buff> affect(char: Char, buffClass: Class<T>, duration: Float): T
        fun <T : Buff> prolong(char: Char, buffClass: Class<T>, duration: Float): T
        fun detach(char: Char, buffClass: Class<out Buff>)
    }

    open fun attachTo(target: Char): Boolean
    open fun detach()
    override fun act(): Boolean
    open fun icon(): Int = NONE
    open fun desc(): String = ""
}
```

### FlavourBuff

Duration-based buffs that expire:

```kotlin
abstract class FlavourBuff : Buff() {
    override fun act(): Boolean {
        detach()
        return true
    }
}
```

---

## Positive Buffs

### Movement & Action

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Speed | `Speed.kt` | 2x movement speed | Variable | Green arrows |
| Levitation | `Levitation.kt` | Float over hazards | 10 turns | Cloud |
| Haste | `Haste.kt` | +50% attack speed | Variable | Lightning |

### Defense & Survival

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Barkskin | `Barkskin.kt` | Bonus armor (+level DR) | Variable | Shield |
| Regeneration | `Regeneration.kt` | HP regen per turn | Permanent | Heart |
| GasesImmunity | `GasesImmunity.kt` | Immune to gas | Variable | Mask |
| Bless | `Bless.kt` | +25% accuracy, evasion | Variable | Halo |

### Vision & Stealth

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Invisibility | `Invisibility.kt` | Enemies can't see | 15 turns | Ghost |
| MindVision | `MindVision.kt` | See all mobs | 20 turns | Eye |
| Light | `Light.kt` | Extended view range | Variable | Sun |
| Awareness | `Awareness.kt` | See traps/doors | 10 turns | Alert |
| Shadows | `Shadows.kt` | Harder to detect | Variable | Shadow |

---

## Negative Buffs (Debuffs)

### Damage Over Time

| Buff | File | Damage | Duration | Cure |
|------|------|--------|----------|------|
| Burning | `Burning.kt` | 1-5/turn | 8 turns | Water |
| Poison | `Poison.kt` | 1-3/turn | Variable | Antidote |
| Bleeding | `Bleeding.kt` | Level/turn | Until healed | Bandage |
| Ooze | `Ooze.kt` | 1/turn | 20 turns | Washing |

### Movement Impairment

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Paralysis | `Paralysis.kt` | Cannot act | 10 turns | Lock |
| Roots | `Roots.kt` | Cannot move | 5 turns | Vines |
| Cripple | `Cripple.kt` | -50% speed | 10 turns | Leg |
| Slow | `Slow.kt` | -50% speed | 10 turns | Snail |
| Vertigo | `Vertigo.kt` | Random movement | 5 turns | Spiral |

### Vision Impairment

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Blindness | `Blindness.kt` | View range = 1 | 10 turns | Blindfold |
| Darkness | `Darkness.kt` | Reduced vision | Variable | Dark |

### Mind Effects

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Charm | `Charm.kt` | Cannot attack source | 10 turns | Heart |
| Sleep | `Sleep.kt` | Unconscious | Until damaged | Zzz |
| Terror | `Terror.kt` | Flee from source | 10 turns | Skull |
| Amok | `Amok.kt` | Attack randomly | 5 turns | Rage |

### Stat Reduction

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Weakness | `Weakness.kt` | -2 STR | Until cured | Down arrow |
| Vulnerable | `Vulnerable.kt` | +25% damage taken | Variable | Crack |

### Temperature

| Buff | File | Effect | Duration | Icon |
|------|------|--------|----------|------|
| Frost | `Frost.kt` | Slowed, freezing | Variable | Snowflake |
| Chill | `Chill.kt` | Slowed | 5 turns | Ice |

---

## Special Buffs

### Combat Mechanics

| Buff | File | Effect | Notes |
|------|------|--------|-------|
| Combo | `Combo.kt` | Track consecutive hits | Gladiator subclass |
| SnipersMark | `SnipersMark.kt` | Extra projectile damage | Sniper subclass |
| Fury | `Fury.kt` | +50% damage at low HP | Berserker subclass |
| Rage | `Rage.kt` | Damage boost | Temporary |

### Hero Mechanics

| Buff | File | Effect | Notes |
|------|------|--------|-------|
| Hunger | `Hunger.kt` | Starvation system | Always active |
| Foresight | `Foresight.kt` | Trap detection | Assassin |

---

## Buff Mechanics

### Applying Buffs

```kotlin
// Apply with default duration
Buff.affect(target, Poison::class.java)

// Apply with specific duration
Buff.affect(target, Invisibility::class.java, 15f)

// Extend existing buff
Buff.prolong(target, Burning::class.java, 5f)

// Remove buff
Buff.detach(target, Paralysis::class.java)
```

### Buff Stacking

Most buffs **do not stack** - applying the same buff extends duration:

```kotlin
class Burning : Buff() {
    var left: Float = DURATION

    override fun attachTo(target: Char): Boolean {
        // If already burning, extend duration
        target.buff(Burning::class.java)?.let { existing ->
            existing.left = max(existing.left, DURATION)
            return false
        }
        return super.attachTo(target)
    }
}
```

### Buff Resistance

Some effects can be resisted:

```kotlin
// Paralysis resistance example
if (Random.Float() < target.resistParalysis()) {
    // Resist the effect
    return false
}
Buff.affect(target, Paralysis::class.java, duration)
```

---

## Detailed Buff Descriptions

### Burning

**Path**: `actors/buffs/Burning.kt`

```kotlin
class Burning : Buff(), Hero.Doom {
    companion object {
        const val DURATION = 8f
    }

    var left: Float = DURATION

    override fun act(): Boolean {
        if (target!!.isAlive) {
            // Deal damage
            target!!.damage(Random.Int(1, 5), this)

            // Spread fire to ground
            if (target!!.pos != -1) {
                Blob.seed(target!!.pos, 2, Fire::class.java)
            }
        }

        left -= TICK
        if (left <= 0 || target!!.HP <= 0) {
            detach()
        } else {
            spend(TICK)
        }

        return true
    }

    override fun onDeath() {
        Badges.validateDeathByFire()
        GLog.n("You burned to death...")
    }
}
```

### Poison

**Path**: `actors/buffs/Poison.kt`

```kotlin
class Poison : Buff() {
    var left: Float = 0f

    companion object {
        fun duration(ch: Char): Float {
            return when (ch) {
                is Hero -> 5f + ch.lvl / 2f
                else -> 10f
            }
        }
    }

    fun set(duration: Float) {
        left = max(left, duration)
    }

    override fun act(): Boolean {
        if (target!!.isAlive) {
            val damage = (left / 3 + 1).toInt()
            target!!.damage(damage, this)
        }

        left -= TICK
        if (left <= 0) {
            detach()
        } else {
            spend(TICK)
        }

        return true
    }
}
```

### Invisibility

**Path**: `actors/buffs/Invisibility.kt`

```kotlin
class Invisibility : FlavourBuff() {
    companion object {
        const val DURATION = 15f

        fun dispel() {
            Dungeon.hero?.buff(Invisibility::class.java)?.let { buff ->
                buff.detach()
                GLog.w("You are no longer invisible!")
            }
        }
    }

    override fun attachTo(target: Char): Boolean {
        if (super.attachTo(target)) {
            target.invisible++
            return true
        }
        return false
    }

    override fun detach() {
        target?.invisible--
        super.detach()
    }
}
```

### Hunger

**Path**: `actors/buffs/Hunger.kt`

```kotlin
class Hunger : Buff() {
    companion object {
        const val HUNGRY = 260f
        const val STARVING = 360f
        const val MAX = 450f
    }

    var level: Float = 0f
    var partialDamage: Float = 0f

    override fun act(): Boolean {
        if (!target!!.isAlive) {
            spend(TICK)
            return true
        }

        level += TICK

        if (level >= STARVING) {
            // Take starvation damage
            partialDamage += TICK * target!!.HT / 1000f
            if (partialDamage >= 1) {
                val damage = partialDamage.toInt()
                partialDamage -= damage
                target!!.damage(damage, this)
            }
        }

        spend(TICK)
        return true
    }

    fun satisfy(energy: Float) {
        level = max(0f, level - energy)
    }

    fun isHungry(): Boolean = level >= HUNGRY
    fun isStarving(): Boolean = level >= STARVING
}
```

---

## Buff Icons

Buffs display icons in the status bar:

```kotlin
object BuffIndicator {
    const val NONE = -1
    const val MIND_VISION = 0
    const val LEVITATION = 1
    const val FIRE = 2
    const val POISON = 3
    const val PARALYSIS = 4
    const val HUNGER = 5
    const val HUNGER_STARVING = 6
    const val SLOW = 7
    const val OOZE = 8
    const val AMOK = 9
    const val TERROR = 10
    const val ROOTS = 11
    const val INVISIBLE = 12
    const val SHADOWS = 13
    const val WEAKNESS = 14
    const val FROST = 15
    const val BLINDNESS = 16
    const val COMBO = 17
    const val FURY = 18
    const val HEALING = 19
    const val ARMOR = 20
    const val HEART = 21
    const val LIGHT = 22
    const val VERTIGO = 23
    // ... etc
}
```

---

## Buff Interactions

### Conflicting Buffs

Some buffs cancel each other:

| Buff A | Buff B | Interaction |
|--------|--------|-------------|
| Burning | Frost | Cancel each other |
| Levitation | Roots | Levitation blocks roots |
| Invisibility | Any action | Dispels invisibility |

### Buff Immunity

Some characters are immune to certain buffs:

| Character | Immunity |
|-----------|----------|
| Fire Elemental | Burning |
| Golem | Poison, Bleeding |
| Wraith | Physical buffs |
| Hero (with item) | Various ring effects |

---

## See Also

- [Actor System](../systems/actor-system.md) - Buff mechanics
- [Mobs](mobs.md) - Enemy abilities
- [Items](items.md) - Buff-granting items
