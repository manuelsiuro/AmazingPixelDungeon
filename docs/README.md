# Amazing Pixel Dungeon Documentation

Welcome to the comprehensive documentation for **Amazing Pixel Dungeon**, an Android roguelike dungeon crawler game built on the Noosa 2D engine.

## Quick Navigation

### Architecture
- [Architecture Overview](architecture/overview.md) - High-level system architecture and entry points
- [Core Systems](architecture/core-systems.md) - Game loop, state management, save/load
- [Package Structure](architecture/package-structure.md) - Complete package organization

### Systems
- [Scene System](systems/scene-system.md) - Scene lifecycle, transitions, and state
- [UI System](systems/ui-system.md) - Components, windows, and input handling
- [Rendering System](systems/rendering-system.md) - Noosa engine, sprites, and animations
- [Actor System](systems/actor-system.md) - Turn-based scheduling and buff system
- [Level Generation](systems/level-generation.md) - Procedural dungeon generation pipeline, room shapes, and decorations
- [LLM System](systems/llm-system.md) - On-device AI text generation for enhanced game content

### Design Documents
- [Village Level Plan](design/village-level-plan.md) - Outdoor village hub at depth 0
- [Item Expansion Plan](design/item-expansion-plan.md) - New weapons, potions, scrolls, and more

### Tools
- [Sprite Generation](tools/sprite-generation.md) - AI-powered pixel art sprite generation tool

### Entity Catalogs
- [Items](entities/items.md) - All weapons, armor, potions, scrolls, rings, wands, and more
- [Mobs](entities/mobs.md) - All enemies organized by dungeon region
- [Levels](entities/levels.md) - All level types and room painters
- [Buffs](entities/buffs.md) - All status effects and buffs
- [Plants](entities/plants.md) - All plant types and their effects

### Diagrams
- [Class Hierarchy](diagrams/class-hierarchy.md) - Inheritance diagrams for core classes
- [Scene Flow](diagrams/scene-flow.md) - Scene transition diagram
- [Game Loop](diagrams/game-loop.md) - Actor/turn system flow

---

## Project Overview

Amazing Pixel Dungeon is a turn-based roguelike with procedurally generated dungeons. Key features include:

- **4 Hero Classes**: Warrior, Mage, Rogue, Huntress (each with subclasses)
- **26 Dungeon Floors**: 5 regions with unique themes and bosses
- **180+ Items**: Weapons, armor, potions, scrolls, rings, wands, crafting materials
- **45+ Enemies**: From rats to demonic horrors
- **Turn-Based Combat**: Strategic action point system

## Technical Stack

| Component | Technology |
|-----------|------------|
| Platform | Android (API 14+) |
| Language | Kotlin |
| Graphics | OpenGL ES 2.0 via Noosa engine |
| Architecture | MVC with singleton state |
| Build System | Gradle |

## Getting Started for Developers

### Prerequisites
- Android Studio 4.0+
- JDK 8+
- Android SDK with API 14+ support

### Building the Project
```bash
./gradlew assembleDebug
```

### Project Structure
```
app/src/main/java/com/watabou/
├── glwrap/          # OpenGL wrapper utilities
├── gltextures/      # Texture management
├── glscripts/       # GL shader scripts
├── input/           # Input handling
├── noosa/           # Core 2D rendering engine
├── pixeldungeon/    # Main game code
└── utils/           # Utility classes
```

### Key Entry Points

1. **Application Entry**: `PixelDungeon.kt` extends `Game.kt`
2. **Game State**: `Dungeon.kt` singleton holds all game state
3. **Main Gameplay**: `GameScene.kt` renders the dungeon
4. **Hero Logic**: `Hero.kt` handles player actions

---

## Code Conventions

- **Kotlin**: All code is in Kotlin with some Java interop
- **Singleton Pattern**: Core managers use Kotlin `object` declarations
- **Bundle Serialization**: Game state saved via `Bundle.kt` key-value system
- **Asset References**: All assets defined in `Assets.kt`

## Contributing

1. Read the [Architecture Overview](architecture/overview.md) first
2. Understand the [Actor System](systems/actor-system.md) for gameplay changes
3. Review the relevant entity catalog before adding new content

---

*Documentation generated for Amazing Pixel Dungeon codebase.*
