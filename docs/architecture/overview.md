# Architecture Overview

This document describes the high-level architecture of Amazing Pixel Dungeon.

## System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      Android Activity                        │
│                     (PixelDungeon.kt)                        │
├─────────────────────────────────────────────────────────────┤
│                       Game Engine                            │
│                        (Game.kt)                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │ Game Loop    │  │   Scenes     │  │  Input Handler   │  │
│  │ step()/draw()│  │  (12 types)  │  │  (Touchscreen)   │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                     Noosa 2D Engine                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │   Sprites    │  │   Camera     │  │   Visual FX      │  │
│  │   Textures   │  │   Viewport   │  │   Particles      │  │
│  └──────────────┘  └──────────────┘  └──────────────────┘  │
├─────────────────────────────────────────────────────────────┤
│                    OpenGL ES 2.0                             │
│                     (GLWrap layer)                           │
└─────────────────────────────────────────────────────────────┘
```

## Entry Point

### PixelDungeon.kt
**Path**: `com/watabou/pixeldungeon/PixelDungeon.kt`

The main application class that extends `Game.kt`. Responsibilities:

- Initialize game preferences and settings
- Configure immersive mode for Android
- Load and manage assets
- Handle application lifecycle (pause, resume)
- Set initial scene (TitleScene)

```kotlin
class PixelDungeon : Game() {
    override fun onCreate() {
        super.onCreate()
        // Initialize preferences
        // Load assets
        // Switch to TitleScene
    }
}
```

### Game.kt
**Path**: `com/watabou/noosa/Game.kt`

The core game engine class providing:

- **Game Loop**: `step()` for logic updates, `draw()` for rendering
- **Frame Timing**: Targets 30 FPS with delta time compensation
- **Scene Management**: `switchScene()` for transitions
- **Input Processing**: Routes touch/key events to scenes

## Core Singleton: Dungeon.kt

**Path**: `com/watabou/pixeldungeon/Dungeon.kt`

The central game state manager implemented as a Kotlin `object`. This singleton holds:

### Game State
```kotlin
object Dungeon {
    var level: Level?       // Current dungeon level
    var hero: Hero?         // Player character
    var depth: Int          // Current floor (1-26)
    var gold: Int           // Collected gold
    var challenges: Int     // Active challenge flags
}
```

### Key Methods
| Method | Purpose |
|--------|---------|
| `init()` | Initialize new game |
| `newLevel()` | Generate next dungeon floor |
| `saveGame()` | Serialize state to file |
| `loadGame()` | Deserialize state from file |
| `saveLevel()` | Save current level state |
| `loadLevel()` | Load level state |

## Serialization System

### Bundle.kt
**Path**: `com/watabou/utils/Bundle.kt`

A key-value serialization system similar to Android's Bundle but optimized for game state:

```kotlin
// Saving
val bundle = Bundle()
bundle.put("hp", hero.HP)
bundle.put("pos", hero.pos)
bundle.put("inventory", hero.belongings.backpack)

// Loading
val hp = bundle.getInt("hp")
val pos = bundle.getInt("pos")
val inventory = bundle.getBag("inventory")
```

### Storable Interface
Classes implement `Bundlable` interface for serialization:

```kotlin
interface Bundlable {
    fun storeInBundle(bundle: Bundle)
    fun restoreFromBundle(bundle: Bundle)
}
```

## Game Loop

The game runs at approximately 30 FPS with the following cycle:

```
┌─────────────────────────────────────────┐
│              Game.step()                 │
│  ┌─────────────────────────────────┐    │
│  │  1. Calculate delta time         │    │
│  │  2. Process input events         │    │
│  │  3. Update current scene         │    │
│  │  4. Process actor queue          │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
                    │
                    ▼
┌─────────────────────────────────────────┐
│              Game.draw()                 │
│  ┌─────────────────────────────────┐    │
│  │  1. Clear screen                 │    │
│  │  2. Update camera                │    │
│  │  3. Draw scene hierarchy         │    │
│  │  4. Swap buffers                 │    │
│  └─────────────────────────────────┘    │
└─────────────────────────────────────────┘
```

## Layer Architecture

### Visual Hierarchy
```
Scene (root)
├── Dungeon Tilemap (terrain)
├── Heaps Layer (items on ground)
├── Mobs Layer (enemies)
├── Hero Sprite
├── Effects Layer (particles, missiles)
├── Fog of War
├── Status Pane (health, gold)
├── Toolbar (action buttons)
└── Windows (modal dialogs)
```

### Update Order
1. Scene.update() cascades to all children
2. Actor.process() handles turn-based logic
3. Sprites update animations
4. Effects update particles
5. Camera follows hero

## State Flow

```
┌────────────┐     ┌────────────┐     ┌────────────┐
│ TitleScene │ ──▶ │ StartScene │ ──▶ │ Interlevel │
└────────────┘     └────────────┘     │   Scene    │
                                      └─────┬──────┘
                                            │
                   ┌────────────────────────┘
                   ▼
┌─────────────────────────────────────────────────────┐
│                    GameScene                         │
│  ┌─────────────────────────────────────────────┐   │
│  │  Active Gameplay                              │   │
│  │  - Actor processing                          │   │
│  │  - Combat resolution                         │   │
│  │  - Level transitions                         │   │
│  └─────────────────────────────────────────────┘   │
└──────────────────────┬──────────────────────────────┘
                       │
       ┌───────────────┼───────────────┐
       ▼               ▼               ▼
┌────────────┐  ┌────────────┐  ┌────────────┐
│  Rankings  │  │  Surface   │  │   Amulet   │
│   Scene    │  │   Scene    │  │   Scene    │
│  (death)   │  │   (win)    │  │(endgame)   │
└────────────┘  └────────────┘  └────────────┘
```

## Key File Locations

| File | Purpose |
|------|---------|
| `PixelDungeon.kt` | Application entry point |
| `Game.kt` | Core engine with game loop |
| `Dungeon.kt` | Game state singleton |
| `Bundle.kt` | Serialization system |
| `Assets.kt` | Asset path definitions |
| `GameScene.kt` | Main gameplay scene |
| `Level.kt` | Dungeon floor base class |
| `Hero.kt` | Player character |
| `Actor.kt` | Turn-based entity base |

## See Also

- [Core Systems](core-systems.md) - Detailed system documentation
- [Package Structure](package-structure.md) - Full package organization
- [Scene System](../systems/scene-system.md) - Scene lifecycle details
- [Actor System](../systems/actor-system.md) - Turn-based mechanics
