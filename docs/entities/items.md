# Items Catalog

This document catalogs all items in Amazing Pixel Dungeon.

## Overview

The game contains **149+ items** across multiple categories:
- Weapons (33 types + 12 enchantments)
- Armor (11 types + 12 glyphs)
- Potions (13 types)
- Scrolls (15 types)
- Rings (13 types)
- Wands (14 types)
- Food (6 types)
- Keys (4 types)
- Quest Items (7 types)
- Miscellaneous items

## Item Base Class

**Path**: `items/Item.kt`

All items extend the base `Item` class:

```kotlin
abstract class Item : Bundlable {
    var quantity: Int = 1
    var level: Int = 0

    var cursed: Boolean = false
    var cursedKnown: Boolean = false
    var levelKnown: Boolean = false

    open val isIdentified: Boolean
        get() = levelKnown && cursedKnown

    open val isUpgradable: Boolean
        get() = true

    open val image: Int
        get() = 0

    open fun actions(hero: Hero): ArrayList<String>
    open fun execute(hero: Hero, action: String)
    open fun doPickUp(hero: Hero): Boolean
    open fun doThrow(hero: Hero)
    open fun upgrade(): Item
    open fun degrade(): Item
}
```

---

## Weapons

**Path**: `items/weapon/`

### Weapon Base

```kotlin
abstract class Weapon : EquipableItem() {
    var enchantment: Enchantment? = null
    abstract val STR: Int  // Strength requirement
    abstract val MIN: Int  // Minimum damage
    abstract val MAX: Int  // Maximum damage

    open fun damageRoll(owner: Hero): Int {
        return Random.IntRange(MIN, MAX) + level
    }
}
```

### Melee Weapons

| Tier | Name | File | STR | Damage | Description |
|------|------|------|-----|--------|-------------|
| 1 | Short Sword | `ShortSword.kt` | 10 | 1-10 | Starting weapon for Warrior |
| 1 | Knuckles | `Knuckles.kt` | 8 | 1-6 | Fast attack speed |
| 1 | Dagger | `Dagger.kt` | 9 | 1-10 | Starting weapon for Rogue |
| 2 | Quarterstaff | `Quarterstaff.kt` | 12 | 2-10 | Good blocking |
| 2 | Mace | `Mace.kt` | 13 | 3-12 | Reliable damage |
| 2 | Spear | `Spear.kt` | 11 | 2-15 | Reach weapon |
| 3 | Sword | `Sword.kt` | 14 | 4-20 | Balanced |
| 3 | Scimitar | `Scimitar.kt` | 13 | 3-15 | Fast swing |
| 4 | Longsword | `Longsword.kt` | 16 | 5-25 | High damage |
| 4 | Battle Axe | `BattleAxe.kt` | 17 | 6-28 | Heavy hits |
| 5 | War Hammer | `WarHammer.kt` | 20 | 8-32 | Devastating |
| 5 | Glaive | `Glaive.kt` | 18 | 5-30 | Best reach |

### Missile Weapons

| Name | File | Damage | Stackable | Description |
|------|------|--------|-----------|-------------|
| Dart | `Dart.kt` | 1-4 | Yes | Basic ranged |
| Incendiary Dart | `IncendiaryDart.kt` | 1-4 | Yes | Sets target on fire |
| Curare Dart | `CurareDart.kt` | 1-4 | Yes | Causes paralysis |
| Shuriken | `Shuriken.kt` | 2-6 | Yes | Fast throwing |
| Javelin | `Javelin.kt` | 3-15 | Yes | Heavy throwing |
| Tomahawk | `Tomahawk.kt` | 4-20 | Yes | Causes bleeding |
| Boomerang | `Boomerang.kt` | 1-6 | No | Returns after throw |

### Weapon Enchantments

| Enchantment | File | Effect |
|-------------|------|--------|
| Blazing | `Fire.kt` | Chance to ignite target |
| Chilling | `Frost.kt` | Chance to freeze target |
| Shocking | `Shock.kt` | Chain lightning damage |
| Venomous | `Poison.kt` | Applies poison |
| Stunning | `Paralysis.kt` | Chance to paralyze |
| Vampiric | `Leech.kt` | Heals on hit |
| Lucky | `Luck.kt` | Increased critical chance |
| Unstable | `Instability.kt` | Random enchant per hit |
| Grim | `Death.kt` | Instant kill chance |
| Eldritch | `Horror.kt` | Terrifies target |
| Wild | `Wild.kt` | Unpredictable effects |
| Tempered | `Tempering.kt` | Reduces degradation |

---

## Armor

**Path**: `items/armor/`

### Armor Base

```kotlin
abstract class Armor : EquipableItem() {
    var glyph: Glyph? = null
    abstract val STR: Int  // Strength requirement
    abstract val dr: Int   // Damage reduction

    open fun proc(attacker: Char, defender: Char, damage: Int): Int {
        return glyph?.proc(this, attacker, defender, damage) ?: damage
    }
}
```

### Armor Types

| Tier | Name | File | STR | DR | Description |
|------|------|------|-----|-----|-------------|
| 1 | Cloth Armor | `ClothArmor.kt` | 10 | 0-1 | Starting armor |
| 2 | Leather Armor | `LeatherArmor.kt` | 11 | 0-3 | Light protection |
| 3 | Mail Armor | `MailArmor.kt` | 13 | 0-5 | Medium protection |
| 4 | Scale Armor | `ScaleArmor.kt` | 15 | 0-7 | Heavy protection |
| 5 | Plate Armor | `PlateArmor.kt` | 17 | 0-10 | Best protection |

### Class Armor

Unlocked after reading Tome of Mastery:

| Class | Armor | File | Special Ability |
|-------|-------|------|-----------------|
| Warrior | Warrior's Armor | `WarriorArmor.kt` | Heroic Leap |
| Mage | Mage's Armor | `MageArmor.kt` | Molten Earth |
| Rogue | Rogue's Armor | `RogueArmor.kt` | Smoke Bomb |
| Huntress | Huntress's Armor | `HuntressArmor.kt` | Spectral Blades |

### Armor Glyphs

| Glyph | File | Effect |
|-------|------|--------|
| Affection | `Affection.kt` | Chance to charm attacker |
| Anti-Entropy | `AntiEntropy.kt` | Freeze/burn attacker |
| Auto-Repair | `AutoRepair.kt` | Regenerates durability |
| Bounce | `Bounce.kt` | Knocks back attacker |
| Displacement | `Displacement.kt` | Teleport on hit |
| Entanglement | `Entanglement.kt` | Roots attacker |
| Flow | `Flow.kt` | Faster in water |
| Metabolism | `Metabolism.kt` | Heals when eating |
| Multiplicity | `Multiplicity.kt` | Creates mirror images |
| Potential | `Potential.kt` | Recharges wands |
| Stench | `Stench.kt` | Emits toxic gas |
| Viscosity | `Viscosity.kt` | Delays damage |

---

## Potions

**Path**: `items/potions/`

| Potion | File | Effect | Thrown Effect |
|--------|------|--------|---------------|
| Potion of Healing | `PotionOfHealing.kt` | Restores all HP | Heals allies |
| Potion of Strength | `PotionOfStrength.kt` | +1 Strength | None |
| Potion of Mind Vision | `PotionOfMindVision.kt` | See all mobs | None |
| Potion of Invisibility | `PotionOfInvisibility.kt` | 15 turns invisible | Creates smoke |
| Potion of Liquid Flame | `PotionOfLiquidFlame.kt` | Sets self on fire | Fire explosion |
| Potion of Frost | `PotionOfFrost.kt` | Freezes self | Freeze area |
| Potion of Toxic Gas | `PotionOfToxicGas.kt` | Poisons self | Poison cloud |
| Potion of Paralytic Gas | `PotionOfParalyticGas.kt` | Paralyzes self | Paralysis cloud |
| Potion of Levitation | `PotionOfLevitation.kt` | 10 turns flight | None |
| Potion of Experience | `PotionOfExperience.kt` | Gain XP | None |
| Potion of Might | `PotionOfMight.kt` | +5 HP, +1 STR | None |
| Potion of Purity | `PotionOfPurity.kt` | Clears debuffs | Purifies area |

---

## Scrolls

**Path**: `items/scrolls/`

| Scroll | File | Effect |
|--------|------|--------|
| Scroll of Identify | `ScrollOfIdentify.kt` | Identifies one item |
| Scroll of Upgrade | `ScrollOfUpgrade.kt` | +1 to equipment |
| Scroll of Enchantment | `ScrollOfEnchantment.kt` | Adds enchant/glyph |
| Scroll of Remove Curse | `ScrollOfRemoveCurse.kt` | Uncurses equipment |
| Scroll of Magic Mapping | `ScrollOfMagicMapping.kt` | Reveals entire floor |
| Scroll of Teleportation | `ScrollOfTeleportation.kt` | Random teleport |
| Scroll of Recharging | `ScrollOfRecharging.kt` | Recharges all wands |
| Scroll of Mirror Image | `ScrollOfMirrorImage.kt` | Creates 2 clones |
| Scroll of Lullaby | `ScrollOfLullaby.kt` | Puts nearby mobs to sleep |
| Scroll of Terror | `ScrollOfTerror.kt` | Terrifies nearby mobs |
| Scroll of Challenge | `ScrollOfChallenge.kt` | Attracts all mobs |
| Scroll of Psionic Blast | `ScrollOfPsionicBlast.kt` | Damages visible mobs |
| Scroll of Wipe Out | `ScrollOfWipeOut.kt` | Kills all visible mobs |
| Scroll of Rage | `ScrollOfRage.kt` | Enrages nearby mobs |

---

## Rings

**Path**: `items/rings/`

All rings have cumulative effects when wearing multiple:

| Ring | File | Effect per Level |
|------|------|------------------|
| Ring of Accuracy | `RingOfAccuracy.kt` | +10% hit chance |
| Ring of Evasion | `RingOfEvasion.kt` | +10% dodge chance |
| Ring of Elements | `RingOfElements.kt` | +10% elemental resist |
| Ring of Haste | `RingOfHaste.kt` | +10% movement speed |
| Ring of Power | `RingOfPower.kt` | +10% damage |
| Ring of Mending | `RingOfMending.kt` | +10% healing received |
| Ring of Detection | `RingOfDetection.kt` | +1 view distance |
| Ring of Shadows | `RingOfShadows.kt` | Reduced enemy vision |
| Ring of Satiety | `RingOfSatiety.kt` | -10% hunger rate |
| Ring of Herbalism | `RingOfHerbalism.kt` | Better plant yields |
| Ring of Thorns | `RingOfThorns.kt` | Damage to melee attackers |
| Ring of Haggler | `RingOfHaggler.kt` | Shop discounts |

---

## Wands

**Path**: `items/wands/`

| Wand | File | Effect | Max Charges |
|------|------|--------|-------------|
| Wand of Magic Missile | `WandOfMagicMissile.kt` | Pure magic damage | 3+ |
| Wand of Lightning | `WandOfLightning.kt` | Chain lightning | 2+ |
| Wand of Firebolt | `WandOfFirebolt.kt` | Fire damage, ignites | 2+ |
| Wand of Disintegration | `WandOfDisintegration.kt` | Piercing beam | 2+ |
| Wand of Poison | `WandOfPoison.kt` | Applies poison | 2+ |
| Wand of Avalanche | `WandOfAvalanche.kt` | Earth damage, stun | 2+ |
| Wand of Amok | `WandOfAmok.kt` | Makes target attack allies | 2+ |
| Wand of Slowness | `WandOfSlowness.kt` | Slows target | 2+ |
| Wand of Blink | `WandOfBlink.kt` | Short teleport | 3+ |
| Wand of Teleportation | `WandOfTeleportation.kt` | Teleports target | 2+ |
| Wand of Regrowth | `WandOfRegrowth.kt` | Creates grass/plants | 2+ |
| Wand of Flock | `WandOfFlock.kt` | Summons sheep | 2+ |
| Wand of Reach | `WandOfReach.kt` | Extended melee | 2+ |

---

## Food

**Path**: `items/food/`

| Food | File | Satiety | Special |
|------|------|---------|---------|
| Ration of Food | `Food.kt` | Full | None |
| Pasty | `Pasty.kt` | Full | Tasty |
| Mystery Meat | `MysteryMeat.kt` | Partial | May cause effects |
| Chargrilled Meat | `ChargrilledMeat.kt` | Partial | Safe to eat |
| Frozen Carpaccio | `FrozenCarpaccio.kt` | Partial | Random buff |
| Overpriced Ration | `OverpricedRation.kt` | Full | Shop only |

---

## Containers

**Path**: `items/bags/`

| Bag | File | Contents |
|-----|------|----------|
| Backpack | (base) | General items |
| Seed Pouch | `SeedPouch.kt` | Seeds only |
| Scroll Holder | `ScrollHolder.kt` | Scrolls only |
| Wand Holster | `WandHolster.kt` | Wands only |
| Keyring | `Keyring.kt` | Keys only |

---

## Keys

**Path**: `items/keys/`

| Key | File | Use |
|-----|------|-----|
| Iron Key | `IronKey.kt` | Opens regular doors |
| Golden Key | `GoldenKey.kt` | Opens golden chests |
| Skeleton Key | `SkeletonKey.kt` | Opens any lock |

---

## Quest Items

**Path**: `items/quest/`

| Item | File | Quest |
|------|------|-------|
| Dried Rose | `DriedRose.kt` | Ghost NPC |
| Rat Skull | `RatSkull.kt` | Rat King |
| Pickaxe | `Pickaxe.kt` | Blacksmith |
| Dark Gold Ore | `DarkGold.kt` | Blacksmith |
| Corpse Dust | `CorpseDust.kt` | Wandmaker |
| Rotberry Seed | `Rotberry.kt` | Wandmaker |
| Phantom Fish | `PhantomFish.kt` | Wandmaker |
| Dwarf Token | `DwarfToken.kt` | Imp |

---

## Miscellaneous Items

| Item | File | Effect |
|------|------|--------|
| Gold | `Gold.kt` | Currency |
| Ankh | `Ankh.kt` | Single resurrection |
| Dewdrop | `Dewdrop.kt` | Minor healing |
| Dew Vial | `DewVial.kt` | Stores dewdrops |
| Bomb | `Bomb.kt` | Explosive |
| Honeypot | `Honeypot.kt` | Spawns friendly bee |
| Torch | `Torch.kt` | Extended vision |
| Weightstone | `Weightstone.kt` | Weapon balance |
| Armor Kit | `ArmorKit.kt` | Upgrade to class armor |
| Lloyd's Beacon | `LloydsBeacon.kt` | Mark/return teleport |
| Tome of Mastery | `TomeOfMastery.kt` | Choose subclass |
| Amulet of Yendor | `Amulet.kt` | Victory condition |

---

## Item Generation

Items are generated by `Generator.kt` with weighted probabilities:

```kotlin
object Generator {
    enum class Category {
        WEAPON, ARMOR, POTION, SCROLL, WAND, RING, SEED, FOOD, GOLD, MISC
    }

    fun random(): Item
    fun random(category: Category): Item
    fun randomWeapon(level: Int): Weapon
    fun randomArmor(level: Int): Armor
}
```

## See Also

- [Mobs](mobs.md) - Enemy catalog
- [Actor System](../systems/actor-system.md) - Combat mechanics
- [Package Structure](../architecture/package-structure.md) - Code organization
