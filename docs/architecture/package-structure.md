# Package Structure

This document describes the complete package organization of Amazing Pixel Dungeon.

## Root Package Layout

```
com.watabou/
├── glwrap/           # OpenGL ES wrapper layer
├── gltextures/       # Texture management
├── glscripts/        # Shader scripts
├── input/            # Input handling
├── noosa/            # 2D game engine
├── pixeldungeon/     # Main game code
└── utils/            # Utility classes
```

## Engine Packages

### com.watabou.glwrap
Low-level OpenGL ES 2.0 wrapper utilities.

| Class | Purpose |
|-------|---------|
| `Attribute.kt` | Vertex attribute handling |
| `Framebuffer.kt` | Off-screen rendering |
| `Matrix.kt` | 4x4 transformation matrices |
| `Program.kt` | Shader program management |
| `Quad.kt` | Quad geometry utilities |
| `Renderbuffer.kt` | Render buffer objects |
| `Shader.kt` | Vertex/fragment shaders |
| `Texture.kt` | Base texture class |
| `Uniform.kt` | Shader uniform variables |
| `Vertexbuffer.kt` | Vertex buffer objects |

### com.watabou.gltextures
Texture loading and caching system.

| Class | Purpose |
|-------|---------|
| `Atlas.kt` | Texture atlas management |
| `Gradient.kt` | Procedural gradients |
| `SmartTexture.kt` | Enhanced texture with filtering |
| `TextureCache.kt` | Global texture cache |

### com.watabou.glscripts
OpenGL shader scripts (resources).

### com.watabou.input
Input abstraction layer.

| Class | Purpose |
|-------|---------|
| `Keys.kt` | Keyboard input handling |
| `Touchscreen.kt` | Touch event processing |

### com.watabou.noosa
Core 2D rendering engine.

| Class | Purpose |
|-------|---------|
| `Game.kt` | Main game loop and lifecycle |
| `Scene.kt` | Scene base class |
| `Camera.kt` | Viewport and transforms |
| `Group.kt` | Visual container |
| `Visual.kt` | Base renderable |
| `Gizmo.kt` | Base scene graph node |
| `Image.kt` | Textured quad rendering |
| `NinePatch.kt` | Scalable UI backgrounds |
| `BitmapText.kt` | Text rendering |
| `BitmapTextMultiline.kt` | Multi-line text |
| `ColorBlock.kt` | Solid color rectangles |
| `SkinnedBlock.kt` | Tiled backgrounds |
| `Animation.kt` | Frame-based animations |
| `TextureFilm.kt` | Sprite sheet slicing |
| `MovieClip.kt` | Animated sprites |
| `Emitter.kt` | Particle emitter |
| `Tilemap.kt` | Tile-based rendering |
| `RenderedText.kt` | System font rendering |
| `audio/` | Audio playback |
| `particles/` | Particle system |
| `tweeners/` | Animation tweening |
| `ui/` | UI components |

### com.watabou.utils
General utility classes.

| Class | Purpose |
|-------|---------|
| `Bundle.kt` | Serialization system |
| `Bundlable.kt` | Serialization interface |
| `PointF.kt` | 2D float point |
| `Point.kt` | 2D integer point |
| `Rect.kt` | Integer rectangle |
| `RectF.kt` | Float rectangle |
| `Random.kt` | Random number generation |
| `SparseArray.kt` | Sparse array utilities |
| `Signal.kt` | Event/observer pattern |
| `Callback.kt` | Callback interface |
| `GameMath.kt` | Math utilities |
| `PathFinder.kt` | A* pathfinding |
| `Graph.kt` | Graph algorithms |
| `BArray.kt` | Boolean array utilities |

---

## Game Packages

### com.watabou.pixeldungeon
Root game package with core classes.

| Class | Purpose |
|-------|---------|
| `PixelDungeon.kt` | Application entry point |
| `Dungeon.kt` | Game state singleton |
| `Assets.kt` | Asset path definitions |
| `Badges.kt` | Achievement system |
| `Bones.kt` | Ghost/death mechanic |
| `Challenges.kt` | Challenge mode flags |
| `Chrome.kt` | UI styling |
| `DungeonTilemap.kt` | Level tilemap rendering |
| `FogOfWar.kt` | Visibility system |
| `GamesInProgress.kt` | Save game tracking |
| `Journal.kt` | Quest journal |
| `Preferences.kt` | Settings management |
| `Rankings.kt` | High scores |
| `ResultDescriptions.kt` | Death/win messages |
| `Statistics.kt` | Game statistics |

### com.watabou.pixeldungeon.actors
Entity system base classes.

```
actors/
├── Actor.kt            # Base turn-based entity
├── Char.kt             # Character with HP, combat
├── blobs/              # Environmental effects
├── buffs/              # Status effects
├── hero/               # Player character
└── mobs/               # Enemies and NPCs
```

#### actors/blobs/
Environmental effects that spread across cells.

| Class | Effect |
|-------|--------|
| `Blob.kt` | Base blob class |
| `Alchemy.kt` | Potion brewing |
| `ConfusionGas.kt` | Causes confusion |
| `Fire.kt` | Burns entities and terrain |
| `Foliage.kt` | Dense vegetation |
| `Freezing.kt` | Freezes water and entities |
| `ParalyticGas.kt` | Causes paralysis |
| `Regrowth.kt` | Plant growth |
| `SacrificialFire.kt` | Sacrificial altar |
| `ToxicGas.kt` | Poison damage |
| `WaterOfAwareness.kt` | Reveals map |
| `WaterOfHealth.kt` | Heals fully |
| `WaterOfTransmutation.kt` | Transforms items |
| `Web.kt` | Immobilizes |
| `WellWater.kt` | Well water base |

#### actors/buffs/
Status effects (34 types). See [Buffs Documentation](../entities/buffs.md).

#### actors/hero/
Player character classes.

| Class | Purpose |
|-------|---------|
| `Hero.kt` | Main player character |
| `HeroClass.kt` | Class enum (4 classes) |
| `HeroSubClass.kt` | Subclass specializations |
| `HeroAction.kt` | Action types (Move, Attack, Cook, UseStation, etc.) |
| `Belongings.kt` | Inventory management |

#### actors/mobs/
Enemies and NPCs (45+ types). See [Mobs Documentation](../entities/mobs.md).

```
mobs/
├── Mob.kt              # Base enemy class
├── Bestiary.kt         # Enemy catalog
├── [43 enemy types]
└── npcs/               # Non-hostile NPCs
    ├── NPC.kt          # Base NPC
    ├── Shopkeeper.kt
    ├── Ghost.kt
    ├── Wandmaker.kt
    ├── Blacksmith.kt
    ├── Imp.kt
    ├── StorageChestNpc.kt    # Placeable per-chest storage
    ├── DimensionalChestNpc.kt # Shared cross-depth storage
    ├── DimensionalStorage.kt  # Shared inventory singleton
    └── [more NPCs]
```

### com.watabou.pixeldungeon.crafting
Crafting system — recipes, stations, and smelting.

| Class | Purpose |
|-------|---------|
| `Recipe.kt` | Recipe data (inputs, output, station) |
| `RecipeInput.kt` | Single recipe ingredient |
| `RecipeRegistry.kt` | All recipes registered at startup |
| `StationType.kt` | Enum: NONE, CRAFTING_TABLE, FURNACE, ENCHANTING_TABLE, ANVIL |
| `CraftingManager.kt` | Crafting logic (check materials, craft) |
| `SmeltingJob.kt` | Single furnace smelting job |
| `SmeltingManager.kt` | Furnace smelting queue |
| `MaterialTag.kt` | Material categorization |
| `MaterialTier.kt` | Material tier levels |
| `EnchantmentRegistry.kt` | Available enchantments, option generation, creation |
| `EnchantmentTier.kt` | Enchantment cost tiers (dust, XP, level) |
| `AnvilManager.kt` | Anvil logic: repair weapons, apply enchanted books |

### com.watabou.pixeldungeon.items
All game items (180+ types). See [Items Documentation](../entities/items.md).

```
items/
├── Item.kt             # Base item class
├── Heap.kt             # Ground item pile
├── Generator.kt        # Random item generation
├── EquipableItem.kt    # Wearable base
├── KindOfWeapon.kt     # Weapon interface
├── armor/              # 11 armor types + 12 glyphs
│   └── crafted/        # 4 crafted armor types
├── bags/               # 6 container types (incl. MaterialBag)
├── crafting/           # Crafting materials + storage/dimensional chest items
├── food/               # 6 food items
├── keys/               # 4 key types
├── potions/            # 13 potion types
├── quest/              # 7 quest items
├── rings/              # 13 ring types
├── scrolls/            # 15 scroll types
├── wands/              # 14 wand types
└── weapon/             # 33 weapon types + 12 enchantments
    └── melee/crafted/  # 6 crafted weapon types
```

### com.watabou.pixeldungeon.levels
Dungeon generation and terrain. See [Levels Documentation](../entities/levels.md).

```
levels/
├── Level.kt            # Base level class
├── RegularLevel.kt     # Procedural generation
├── Terrain.kt          # Terrain constants
├── Room.kt             # Room generation
├── SewerLevel.kt       # Floors 1-4
├── SewerBossLevel.kt   # Floor 5 (Goo)
├── PrisonLevel.kt      # Floors 6-9
├── PrisonBossLevel.kt  # Floor 10 (Tengu)
├── CavesLevel.kt       # Floors 11-14
├── CavesBossLevel.kt   # Floor 15 (DM-300)
├── CityLevel.kt        # Floors 16-19
├── CityBossLevel.kt    # Floor 20 (King)
├── HallsLevel.kt       # Floors 22-24
├── HallsBossLevel.kt   # Floor 25 (Eye)
├── LastShopLevel.kt    # Floor 21
├── LastLevel.kt        # Floor 26
├── DeadEndLevel.kt     # Beyond 26
├── features/           # Level features
├── painters/           # Room decorators
└── traps/              # Trap types
```

### com.watabou.pixeldungeon.plants
Plant types (9). See [Plants Documentation](../entities/plants.md).

| Class | Effect |
|-------|--------|
| `Plant.kt` | Base plant class |
| `Dreamweed.kt` | Causes sleep |
| `Earthroot.kt` | Damage absorption |
| `Fadeleaf.kt` | Teleportation |
| `Firebloom.kt` | Fire explosion |
| `Icecap.kt` | Freezing |
| `Rotberry.kt` | Quest item |
| `Sorrowmoss.kt` | Poison effect |
| `Sungrass.kt` | Healing over time |

### com.watabou.pixeldungeon.scenes
Game scenes (13 types). See [Scene System](../systems/scene-system.md).

| Scene | Purpose |
|-------|---------|
| `PixelScene.kt` | Base scene class |
| `TitleScene.kt` | Main menu |
| `StartScene.kt` | Character creation |
| `InterlevelScene.kt` | Loading transitions |
| `GameScene.kt` | Main gameplay |
| `IntroScene.kt` | Game introduction |
| `SurfaceScene.kt` | Victory screen |
| `AmuletScene.kt` | Endgame choice |
| `RankingsScene.kt` | High scores |
| `BadgesScene.kt` | Achievements |
| `AboutScene.kt` | Credits/info |
| `CellSelector.kt` | Target selection |

### com.watabou.pixeldungeon.ui
UI components (31 classes). See [UI System](../systems/ui-system.md).

```
ui/
├── Window.kt           # Base modal window
├── StatusPane.kt       # Hero status HUD
├── Toolbar.kt          # Action toolbar
├── HealthIndicator.kt  # HP display
├── BuffIndicator.kt    # Buff icons
├── QuickSlot.kt        # Quick access slots
├── ItemSlot.kt         # Inventory slot
├── RedButton.kt        # Standard button
├── CheckBox.kt         # Toggle checkbox
├── ScrollPane.kt       # Scrollable area
├── GameLog.kt          # Combat messages
├── Banner.kt           # Header banner
├── Toast.kt            # Notifications
└── [18 more components]
```

### com.watabou.pixeldungeon.windows
Modal dialogs (31 types).

| Window | Purpose |
|--------|---------|
| `WndBag.kt` | Inventory display |
| `WndItem.kt` | Item details |
| `WndHero.kt` | Character sheet |
| `WndInfoMob.kt` | Enemy info |
| `WndSettings.kt` | Game settings |
| `WndGame.kt` | Pause menu |
| `WndMessage.kt` | Simple message |
| `WndOptions.kt` | Choice dialog |
| `WndTradeItem.kt` | Shop interface |
| `WndBlacksmith.kt` | Forge dialog |
| `WndJournal.kt` | Quest log |
| `WndCatalogus.kt` | Item catalog |
| `WndStory.kt` | Story text |
| `WndResurrect.kt` | Ankh revival |
| `WndChallenges.kt` | Challenge selection |
| `WndCrafting.kt` | Crafting table interface |
| `WndFurnace.kt` | Furnace smelting interface |
| [14 more windows] | |

### com.watabou.pixeldungeon.effects
Visual effects and particles.

```
effects/
├── Effects.kt          # Effect utilities
├── BadgeBanner.kt      # Achievement display
├── BlobEmitter.kt      # Blob particles
├── CellEmitter.kt      # Cell effects
├── CheckedCell.kt      # Movement indicator
├── DeathRay.kt         # Beam effects
├── Degradation.kt      # Item degradation
├── EmoIcon.kt          # Emotion bubbles
├── Enchanting.kt       # Enchant sparkles
├── Fireball.kt         # Explosion effect
├── Flare.kt            # Light flare
├── FloatingText.kt     # Damage numbers
├── Halo.kt             # Aura effect
├── IceBlock.kt         # Freeze visual
├── Identification.kt   # ID sparkle
├── Lightning.kt        # Lightning bolt
├── MagicMissile.kt     # Spell projectile
├── Pushing.kt          # Knockback
├── Ripple.kt           # Water ripple
└── particles/          # Particle definitions
```

### com.watabou.pixeldungeon.sprites
Character and item sprites.

| Class | Purpose |
|-------|---------|
| `CharSprite.kt` | Base character sprite |
| `HeroSprite.kt` | Player sprite |
| `MobSprite.kt` | Enemy sprite base |
| `ItemSprite.kt` | Item rendering |
| `ItemSpriteSheet.kt` | Item sprite atlas |
| `MissileSprite.kt` | Projectile sprites |
| `PlantSprite.kt` | Plant visuals |
| `TrapSprite.kt` | Trap visuals |
| `[mob-specific sprites]` | Individual enemies |

### com.watabou.pixeldungeon.mechanics
Game mechanics utilities.

| Class | Purpose |
|-------|---------|
| `Ballistica.kt` | Line-of-sight, projectiles |
| `ShadowCaster.kt` | Visibility calculation |

---

## Package Statistics

| Category | Package Count | Class Count |
|----------|---------------|-------------|
| Engine | 6 | ~50 |
| Actors | 5 | ~130 |
| Items | 10 | ~150 |
| Levels | 4 | ~60 |
| UI/Scenes | 4 | ~80 |
| Effects | 2 | ~30 |
| **Total** | **~31** | **~500** |

## See Also

- [Architecture Overview](overview.md)
- [Core Systems](core-systems.md)
- [Entity Catalogs](../entities/items.md)
