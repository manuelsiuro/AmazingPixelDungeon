# Outdoor Village Level at Depth 0

## Overview

The game starts at depth 0, an outdoor village that serves as a hub with shops where players prepare before entering the dungeon. The player can always return to the village by ascending from depth 1.

## Design Choices

- **Always returnable**: Players can ascend from depth 1 to return to the village at any time
- **Minor threats**: 1-2 rats roaming the outskirts for tutorial combat
- **Varied shops**: Weapon shop, potion shop, and tavern with distinct inventories
- **New tileset**: Outdoor-themed pixel art (placeholder: City tileset)

## Layout (32x32 grid)

- **Weapon Shop** (northwest): Shopkeeper NPC + weapon/armor items as FOR_SALE heaps
- **Potion Shop** (northeast): Shopkeeper NPC + potions/scrolls as FOR_SALE heaps
- **Tavern** (south-west): Food vendor NPC + food/supplies as FOR_SALE heaps
- **Central square**: Grass, paths, a well, a signpost
- **Entrance** (north edge): Gate representing the world exit
- **Exit** (south-center): Stairs down to the dungeon

## Shop Inventories

**Weapon Shop**: ShortSword, Knuckles, ClothArmor, LeatherArmor, Dart x3
**Potion Shop**: PotionOfHealing, 2 random potions, ScrollOfIdentify, ScrollOfMagicMapping, SeedPouch
**Tavern**: Food x2, CheeseWedge, Torch, Weightstone

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
