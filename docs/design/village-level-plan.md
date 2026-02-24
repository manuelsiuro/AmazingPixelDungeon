# Outdoor Village Level at Depth 0

## Overview

The game starts at depth 0, an outdoor village that serves as a hub with shops where players prepare before entering the dungeon. The player can always return to the village by ascending from depth 1.

## Design Choices

- **Always returnable**: Players can ascend from depth 1 to return to the village at any time
- **Minor threats**: 1-2 rats roaming the outskirts for tutorial combat
- **Varied shops**: Weapon shop, potion shop, and tavern with distinct inventories
- **New tileset**: Outdoor-themed pixel art (placeholder: City tileset)

## Layout (128x128 grid)

- **Village Square** (center-north, ~20-44, 5-20): Shops, elder, healing well, signpost, campfire
- **Weapon Shop** (northwest): Shopkeeper NPC + weapon/armor items as FOR_SALE heaps
- **Potion Shop** (northeast): Shopkeeper NPC + potions/scrolls as FOR_SALE heaps
- **Tavern** (south of square): Food vendor NPC + food/supplies as FOR_SALE heaps
- **Workshop Zone** (SW, ~8-25, 26-45): Crafting table, furnace, enchanting table, anvil
- **Farming Zone** (SW lower, ~8-25, 46-58): Farmland, water, crop seeds, farming tools
- **Mining Zone** (SE, ~42-58, 27-50): Geological wall samples (dirt/stone/granite/obsidian), ore wall samples, all 4 pickaxe tiers, extra crafting materials
- **Building Zone** (SE lower, ~42-58, 48-58): Open 12x10 EMPTY area for placing fortifications, pre-dropped building items
- **Forest Zone** (east, ~80-122, 10-62): Large grass area with 6 thematic tree clusters, lumber yard, and paths
- **Entrance** (north edge, ~33,2): Gate representing the world exit
- **Exit** (south-center, ~33,61): Stairs down to the dungeon

## Shop Inventories

**Weapon Shop**: ShortSword, Knuckles, ClothArmor, LeatherArmor, Dart x3
**Potion Shop**: PotionOfHealing, 2 random potions, ScrollOfIdentify, ScrollOfMagicMapping, SeedPouch
**Tavern**: Food x2, CheeseWedge, Torch, Weightstone

## Village Polish (Phase 2)

Enhancements added to make the village feel alive and reward exploration.

### Atmospheric Particles
- **Wind on grass**: Sparse `WindParticle.Wind` emitters on GRASS tiles (1-in-8 chance)
- **Leaves on hedges**: `VillageLeaf` emitters on HIGH_GRASS tiles using `LeafParticle.LEVEL_SPECIFIC` with the village's green color1/color2 (1-in-6 chance)
- **Campfire smoke**: `CampfireSmoke` emitter using `SmokeParticle.FACTORY` alongside existing flame particles

### Village Garden (east of center, x=20-26, y=17-20)
- HIGH_GRASS border with GRASS interior, flanked by two STATUE tiles
- `Foliage` blob seeded over the area (golden light shafts, grants Shadows buff)
- Sungrass (healing) and Brightcap (light) plants for the player to discover

### Herbalist's Corner (west of center, x=5-9, y=13-14)
- Small EMPTY_SP paved area with BOOKSHELF tiles and an ALCHEMY tile
- Functional `Alchemy` blob — players can brew potions from seeds before entering the dungeon

### Hidden Stash (behind weapon shop)
- 3x1 alcove at y=3 behind the weapon shop's north wall
- SECRET_DOOR at pos(8,4) — rewards curious players who search walls
- CHEST containing a random item: Honeypot, Ankh, HolyWater, or SmokeBomb x2

### Pond-side Provisions (SE corner)
- STATUE at pos(20,22) near the pond
- FrostBerry food pickup
- 1-in-3 chance of a bonus Sungrass seed

### Forest Zone (east, x=80-122, y=10-62)
Large grass area for demonstrating the woodcutting system. Includes a lumber yard at (75,20) with a signpost and all 4 axe tiers.

**Tree Clusters (31 trees total):**
- **Oak Grove** (8 trees, ~85-95, 15-25): TREE_OAK, medium hardness
- **Birch Copse** (6 trees, ~100-110, 15-25): TREE_BIRCH, soft
- **Pine Stand** (5 trees, ~85-95, 30-40): TREE_PINE, hard
- **Maple Cluster** (4 trees, ~100-110, 30-40): TREE_MAPLE, medium
- **Willow Area** (4 trees + pond, ~85-95, 45-55): TREE_WILLOW, soft
- **Fruit Orchard** (5 trees, ~100-110, 45-55): TREE_FRUIT, soft

**Paths:** Main east-west path at y=22 (40 tiles), north-south path at x=98 (48 tiles)

## Files Changed

| File | Action |
|------|--------|
| `levels/VillageLevel.kt` | CREATE - Hand-crafted village level |
| `assets/tiles_village.png` | CREATE - Outdoor tileset (placeholder from City) |
| `assets/water_village.png` | CREATE - Water texture (placeholder from City) |
| `Assets.kt` | ADD 2 constants |
| `Dungeon.kt` | MODIFY init(), createLevelForDepth(), deleteGame() |
| `InterlevelScene.kt` | MODIFY descend(), boss sound, region name, music |
| `Hero.kt` | MODIFY actAscend() depth==1 behavior |
| `GameScene.kt` | MODIFY story chapter, welcome message |
| `WndStory.kt` | ADD ID_VILLAGE chapter |
| `StatusPane.kt` | MODIFY depth display for depth 0 |
