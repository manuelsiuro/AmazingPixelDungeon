# Plan: Comprehensive Item Expansion for Amazing Pixel Dungeon

## Context

The game currently has a solid but limited item pool. Many mob kills yield nothing beyond XP, weapon/armor tiers have only 2 choices each, the MISC category has just 2 items (Bomb, Honeypot), and there are no unique/artifact items. This plan catalogs every existing item and proposes a prioritized expansion across all categories to make the game deeper, more rewardable, and more varied.

---

## Current Item Inventory (Complete Census)

### Melee Weapons (11 types, 5 tiers)
| Tier | Weapons | STR | Notes |
|------|---------|-----|-------|
| T1 | Knuckles, Dagger, ShortSword | 10-11 | ShortSword has 0 spawn prob (starting only) |
| T2 | Quarterstaff, Spear | 12 | Only 2 choices |
| T3 | Mace, Sword | 14 | Only 2 choices |
| T4 | Longsword, BattleAxe | 16 | Only 2 choices |
| T5 | WarHammer, Glaive | 18 | Only 2 choices |

### Missile Weapons (7 types)
Dart(0 prob), Shuriken, Javelin, Tamahawk, IncendiaryDart, CurareDart, Boomerang(0 prob, unique)

### Weapon Enchantments (11)
Fire(10), Poison(10), Shock(6), Instability(3), Tempering(3), Paralysis(2), Slow(2), Horror(2), Luck(2), Death(1), Leech(1)

### Armor (5 tiers + 4 class armors)
Cloth, Leather, Mail, Scale, Plate + Warrior/Rogue/Mage/Huntress special

### Armor Glyphs (11, all equal weight)
Bounce, Affection, AntiEntropy, Multiplicity, Potential, Metabolism, Stench, Viscosity, Displacement, Entanglement, AutoRepair

### Rings (12, 2 disabled)
Active: Mending, Detection, Shadows, Power, Herbalism, Accuracy, Evasion, Satiety, Haste, Elements
Disabled (0 prob): Haggler, Thorns

### Potions (12, 2 special-only)
Random: Healing(45), MindVision(20), ToxicGas(15), LiquidFlame(15), Purity(12), ParalyticGas(10), Levitation(10), Invisibility(10), Frost(10), Experience(4)
Special only (0 prob): Strength, Might

### Scrolls (12, 1 special-only)
Random: Identify(30), MagicMapping(15), RemoveCurse(15), Challenge(12), Teleportation(10), Recharging(10), Terror(8), Lullaby(8), MirrorImage(6), PsionicBlast(4), Enchantment(1)
Special only (0 prob): Upgrade

### Wands (13, 1 special-only)
Random: Firebolt(15), Lightning(15), Blink(11), Teleportation(10), Slowness(10), Poison(10), Amok(10), Flock(10), Regrowth(6), Reach(6), Disintegration(5), Avalanche(5)
Special only (0 prob): MagicMissile

### Food (16 types)
Standard: Food(4), StaleRation(3), Apple(2), Pasty(1), SmokeyBacon(1), MushroomSoup(1), HoneyBread(0.5), PixieDustCake(0.3), ElvenWaybread(0.2), MysteryMeat(0)
Cooked: ChargrilledMeat, FrozenCarpaccio
Shop: OverpricedRation, CheeseWedge, DragonPepper, FrostBerry

### Plants/Seeds (8)
Firebloom, Icecap, Sorrowmoss, Dreamweed, Sungrass, Earthroot, Fadeleaf, Rotberry(0 prob)

### Bags (4): SeedPouch, ScrollHolder, WandHolster, Keyring

### Misc (2): Bomb(2), Honeypot(1)

### Special/Utility Items
Ankh, DewVial, Dewdrop, Torch, ArmorKit, Weightstone, TomeOfMastery, LloydsBeacon, Gold

### Buffs/Debuffs (~30)
Damaging: Burning, Poison, Bleeding, Ooze
Control: Paralysis, Frost, Slow, Terror, Vertigo, Charm, Amok, Roots, Cripple, Blindness, Weakness
Beneficial: Regeneration, Invisibility, Shadows, Light, Levitation, MindVision, Awareness, Barkskin, Fury, Rage, Speed, Combo, SnipersMark, GasesImmunity

---

## Identified Gaps

1. **Tiers 2-5 have only 2 melee weapons each** — minimal player choice
2. **MISC category is tiny** — only Bomb and Honeypot for tactical consumables
3. **Most mobs drop nothing** — only Skeleton, Thief, and Bat have loot tables
4. **No artifact/unique item system** — nothing to define a run's identity
5. **Only 8 plant types** — seed pouch feels thin
6. **Two rings permanently disabled** — wasted design space
7. **No throwable crowd-control items** — no nets, smoke bombs, etc.
8. **Limited enchantment/glyph variety** — 11 each gets repetitive

---

## Proposed Item Expansion

### TIER 1 — High Priority (biggest gameplay impact, moderate effort)

#### 1A. Four New Melee Weapons (one per T2-T5)

Each tier gains a 3rd weapon with a distinct combat identity:

| Weapon | Tier | ACU | DLY | STR | Identity |
|--------|------|-----|-----|-----|----------|
| **Whip** | 2 | 0.8 | 0.8 | 12 | Fast but inaccurate; low damage per hit, many hits per turn |
| **Scimitar** | 3 | 1.2 | 1.0 | 14 | Highly accurate, slightly lower damage |
| **Halberd** | 4 | 1.0 | 1.5 | 16 | Slow devastator; fewer turns but massive per-hit damage |
| **Greataxe** | 5 | 0.8 | 1.5 | 20 | Slowest weapon in game, highest damage ceiling |

**Files to create**: `items/weapon/melee/{Whip,Scimitar,Halberd,Greataxe}.kt` (~12 lines each, pattern: `Sword.kt`)
**Files to modify**: `Generator.kt` (add to WEAPON classes/probs, prob=1f each), `ItemSpriteSheet.kt` (4 new constants), `items.png` (4 sprites)
**Complexity**: LOW

#### 1B. Three Throwable Consumables

Expand MISC from 2 items to 5:

| Item | Effect | Price | Gen Prob |
|------|--------|-------|----------|
| **Smoke Bomb** | Blindness (3 turns) in 3x3 area; if self-targeted, also grants Invisibility (3 turns) | 15g | 1.5f |
| **Holy Water** | Bonus damage to undead (Skeleton, Wraith); self-use removes curse from 1 random equipped item | 20g | 1f |
| **Throwing Net** | Roots (5 turns) + Cripple (3 turns) on target mob, no damage | 12g | 1.5f |

**Files to create**: `items/{SmokeBomb,HolyWater,ThrowingNet}.kt` (pattern: `Bomb.kt`)
**Files to modify**: `Generator.kt` (add to MISC), `ItemSpriteSheet.kt`, `items.png`
**Complexity**: MEDIUM (Smoke Bomb needs NEIGHBOURS9 iteration; Holy Water needs undead class check)

#### 1C. Mob Loot Table Expansion

Give more mobs meaningful drops (no new item classes needed):

| Mob | Proposed Loot | Chance | Rationale |
|-----|---------------|--------|-----------|
| Rat | `Generator.Category.FOOD` | 10% | Rats eat dungeon food |
| Piranha | `MysteryMeat()` | 100% | They're fish — always drop meat |
| Wraith | `Generator.Category.SCROLL` | 15% | Ethereal magic beings |
| Golem | `Generator.Category.RING` | 3% | Embedded gems |
| Albino (rare) | `PotionOfHealing()` | 25% | Pure/healing thematic |
| Bandit (rare) | `Gold(Random.Int(50, 120))` | 80% | Bandits hoard gold |
| Shielded Brute (rare) | `Generator.Category.ARMOR` | 15% | Heavily armored |
| Senior Monk (rare) | `Generator.Category.WAND` | 10% | Masters of craft |
| Acidic Scorpio (rare) | `PotionOfToxicGas()` | 30% | Acid/poison theme |

**Files to modify**: ~9 mob files in `actors/mobs/` (1-3 lines each in `init {}` block)
**Complexity**: LOW (just set `loot` and `lootChance` properties)

#### 1D. Three New Weapon Enchantments

| Enchantment | Weight | Effect | Glow |
|-------------|--------|--------|------|
| **Vorpal** (Critical) | 2 | 10% + 2%/level chance to deal triple damage | Deep crimson (0x880000) |
| **Chaining** | 3 | Chance to chain a 50% damage bonus attack to an adjacent enemy | Orange (0xFF6600) |
| **Draining** | 2 | Against magic mobs (Shaman, Warlock): delays ranged attack 2 turns. Others: Weakness 3 turns | Purple (0x6600CC) |

**Files to create**: `items/weapon/enchantments/{Vorpal,Chaining,Draining}.kt` (pattern: `Leech.kt`)
**Files to modify**: `Weapon.kt` companion — add to `enchants` and `chances` arrays
**Complexity**: LOW-MEDIUM (no sprites needed, just proc logic)

#### 1E. Three New Armor Glyphs

| Glyph | Weight | Effect |
|-------|--------|--------|
| **Fortification** | 1 | 30% + 5%/level chance to reduce incoming damage by 25% |
| **Thorny** | 1 | Always reflects `1 + armor.level()` damage back to melee attackers |
| **Swiftness** | 1 | 25% + 5%/level chance to grant 1-turn Speed buff after being hit |

**Files to create**: `items/armor/glyphs/{Fortification,Thorny,Swiftness}.kt` (pattern: `Bounce.kt`)
**Files to modify**: `Armor.kt` companion — add to `glyphs` and `chances` arrays
**Complexity**: LOW-MEDIUM (no sprites needed)

---

### TIER 2 — Medium Priority (more variety, moderate effort)

#### 2A. Four New Plants/Seeds

| Plant | Step-on Effect | Seed Alchemy |
|-------|---------------|--------------|
| **Thornvine** | Bleeding (5 dmg) + Cripple (3 turns) | PotionOfParalyticGas |
| **Mistbloom** | 3x3 mist that blocks line of sight for 5 turns | PotionOfInvisibility |
| **Brightcap** | Light buff (10 turns) + reveals traps/hidden doors nearby | PotionOfMindVision |
| **Venomroot** | 3x3 poison gas cloud (weaker than Sorrowmoss, area-effect) | PotionOfToxicGas |

**Files to create**: 4 files in `plants/` (each with Plant class + inner Seed class)
**Files to modify**: `Generator.kt` (SEED category), `ItemSpriteSheet.kt`, `items.png`, `plants.png`
**Complexity**: MEDIUM (4 plants x plant sprite + seed sprite)

#### 2B. Three New Missile Weapons

| Weapon | STR | Damage | Special |
|--------|-----|--------|---------|
| **Throwing Knife** | 9 | 1-4 | DLY 0.5 (double speed) — the rapid-fire option |
| **Bolas** | 12 | 2-8 | Applies Slow (5 turns) on hit |
| **Explosive Bolt** | 14 | 3-12 | Splash damage to NEIGHBOURS4 for 50% of main damage |

**Files to create**: 3 files in `items/weapon/missiles/`
**Files to modify**: `Generator.kt` (WEAPON category), `ItemSpriteSheet.kt`, `items.png`
**Complexity**: LOW-MEDIUM

#### 2C. Two New Potions

| Potion | Effect (drink) | Effect (throw) | Prob |
|--------|---------------|----------------|------|
| **Potion of Speed** | Speed buff 10 turns (all actions 50% time) | No shatter effect | 8f |
| **Potion of Shielding** | Temporary `10 + depth` HP shield (absorbs damage, lasts 20 turns) | 5 HP shield to character at impact | 10f |

**Files to create**: 2 files in `items/potions/`, potentially `actors/buffs/Shield.kt`
**Files to modify**: `Potion.kt` (expand colors/images arrays 12 -> 14), `Generator.kt`, `ItemSpriteSheet.kt`, `items.png`
**Complexity**: MEDIUM-HIGH (Shielding needs a new buff that intercepts `Char.damage()`)

#### 2D. Two New Scrolls

| Scroll | Effect | Prob |
|--------|--------|------|
| **Scroll of Transmutation** | Transforms item into another of same category/tier, preserving upgrade level and curse status | 4f |
| **Scroll of Foresight** | Reveals all traps, hidden doors, secret rooms on current floor + Awareness buff (50 turns) | 8f |

**Files to create**: 2 files in `items/scrolls/`
**Files to modify**: `Scroll.kt` (expand runes/images arrays 12 -> 14), `Generator.kt`, `ItemSpriteSheet.kt`, `items.png`
**Complexity**: MEDIUM-HIGH (Transmutation needs per-category item swapping logic)

#### 2E. Shop Inventory Enhancements

| Shop (Depth) | New Items |
|-------------|-----------|
| 6 (Prison) | Throwing Net x2, random Seed x2 |
| 11 (Caves) | Smoke Bomb x2, Holy Water x2 |
| 16 (Metropolis) | Random Missile x5, extra Ankh |
| 21 (Halls) | Potion of Speed (if added), Bomb x3 |

**Files to modify**: `levels/painters/ShopPainter.kt`
**Complexity**: LOW

---

### TIER 3 — Nice to Have (creative additions, higher effort)

#### 3A. Artifact System (5 unique items)
New equipment slot; artifacts level up through use, not scrolls. Each defines a run's identity:
- **Chalice of Blood**: Regen that improves as you take damage
- **Sandals of Nature**: Seeds grow into plants instantly
- **Ethereal Chains**: Pull yourself to a target (mobility active)
- **Horn of Plenty**: Slowly generates food charges
- **Cloak of Shadows**: Activatable invisibility (Rogue-themed)

**Complexity**: HIGH (new Artifact base class, new Belongings slot, UI for artifact equip)

#### 3B. Alchemy/Crafting System
Combine 2 seeds at an Alchemy Pot (new terrain) to create enhanced potions or new items. Specific recipes produce known results; unknown combos produce random potions.

**Complexity**: HIGH (new terrain, new UI window, recipe system)

#### 3C. Cursed Item Rework
Make cursed items dual-edged: e.g., cursed weapon = +30% damage but 10% chance to self-harm per swing. Transforms curses from "always bad" to risk/reward decisions.

**Complexity**: MEDIUM-HIGH

#### 3D. Two New Wands
- **Wand of Frost**: Ice counterpart to Firebolt; frozen enemies take double next physical hit
- **Wand of Transfusion**: Damages target and heals hero (or vice versa at high charges)

**Complexity**: MEDIUM

#### 3E. Class-Specific Starting Consumables
Each class starts with one unique single-use item:
- Warrior: Battle Horn (frighten all visible enemies)
- Mage: Arcane Catalyst (fully recharge one wand)
- Rogue: Smoke Pellet (5 turns invisibility)
- Huntress: Tracking Arrow (reveals target 20 turns through walls)

**Complexity**: LOW-MEDIUM

---

## Implementation Sequencing

| Phase | Items | Sprites Needed | Est. New Files |
|-------|-------|---------------|---------------|
| **Phase 1** | Mob loot (1C), Enchantments (1D), Glyphs (1E) | 0 | 6 |
| **Phase 2** | Melee weapons (1A), Throwables (1B), Missiles (2B) | 10 | 10 |
| **Phase 3** | Plants (2A), Potions (2C), Scrolls (2D) | 12 | 10 |
| **Phase 4** | Shops (2E), Artifacts (3A), Alchemy (3B), Wands (3D), Cursed rework (3C), Class items (3E) | 8+ | 15+ |

## Sprite Sheet Status

Current: `items.png` uses indices 0-137 (138 slots). Highest used = 137 (PIXIE_DUST_CAKE).
Indices 138+ are available. Sheet needs to be extended for new rows (8 sprites per row at 16x16 in a 128px wide sheet).

## Key Files for Implementation

| File | Role |
|------|------|
| `items/Generator.kt` | Central item registration — every new item added here |
| `sprites/ItemSpriteSheet.kt` | Sprite index constants |
| `items/weapon/Weapon.kt` | Enchantment registration (companion object) |
| `items/armor/Armor.kt` | Glyph registration (companion object) |
| `items/weapon/melee/MeleeWeapon.kt` | Base class for new melee weapons |
| `items/weapon/missiles/MissileWeapon.kt` | Base class for new missiles |
| `items/potions/Potion.kt` | Potion color/sprite system (expand for new potions) |
| `items/scrolls/Scroll.kt` | Scroll rune/sprite system (expand for new scrolls) |
| `levels/painters/ShopPainter.kt` | Shop inventory |
| `actors/mobs/*.kt` | Individual mob loot tables |

## Verification

1. `./gradlew :app:compileDebugKotlin` after each phase
2. Play-test: verify new items spawn at expected depths
3. Verify new weapon STR gating works with Generator's smart weapon selection
4. Verify new enchantments/glyphs proc correctly in combat
5. Verify NO_FOOD challenge still converts bonus food drops to gold
6. Verify mob loot changes don't affect non-hostile NPCs
