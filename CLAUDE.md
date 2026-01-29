# Claude Code Instructions for Amazing Pixel Dungeon

## Project Overview

Amazing Pixel Dungeon is an Android roguelike dungeon crawler built on the Noosa 2D engine. The codebase has been converted from Java to Kotlin.

## Documentation

**Important:** Comprehensive documentation exists in the `docs/` folder. Always consult these before making changes:

| Documentation | Purpose |
|---------------|---------|
| `docs/README.md` | Documentation index and quick navigation |
| `docs/architecture/overview.md` | High-level system architecture |
| `docs/architecture/core-systems.md` | Game loop, state management, save/load |
| `docs/architecture/package-structure.md` | Complete package organization |
| `docs/systems/scene-system.md` | Scene lifecycle and transitions |
| `docs/systems/ui-system.md` | UI components, windows, input handling |
| `docs/systems/rendering-system.md` | Noosa engine, sprites, animations |
| `docs/systems/actor-system.md` | Turn-based scheduling and buffs |
| `docs/entities/` | Catalogs for items, mobs, levels, buffs, plants |
| `docs/diagrams/` | Class hierarchy, scene flow, game loop diagrams |

## Build Commands

```bash
# Standard debug build
./gradlew assembleDebug

# Compile Kotlin only (faster for syntax checking)
./gradlew :app:compileDebugKotlin

# Clean build
./gradlew clean assembleDebug
```

## Project Structure

```
app/src/main/java/com/watabou/
├── glwrap/          # OpenGL wrapper utilities
├── gltextures/      # Texture management
├── noosa/           # Core 2D rendering engine
├── pixeldungeon/    # Main game code
│   ├── actors/      # Hero, mobs, buffs
│   ├── items/       # All item types
│   ├── levels/      # Dungeon generation
│   ├── scenes/      # Game scenes (menus, gameplay)
│   ├── sprites/     # Sprite definitions
│   ├── ui/          # UI components
│   ├── windows/     # Modal windows/dialogs
│   └── utils/       # Game utilities
└── utils/           # General utilities
```

## Kotlin Conventions

### Singleton Objects (NOT Java-style INSTANCE)

Core managers use Kotlin `object` declarations. Call methods directly:

```kotlin
// CORRECT - Kotlin object
Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
TextureCache.createSolid(0xFF4A4D44.toInt())

// WRONG - Java singleton pattern
Sample.INSTANCE.play(...)  // Does not exist in Kotlin
```

Key singleton objects:
- `Sample` - Audio playback
- `TextureCache` - Texture management
- `Dungeon` - Game state
- `GameMath` - Math utilities

### Nullability Patterns

The codebase has nullable core state. Always handle:

```kotlin
// Dungeon.hero is Hero? (nullable)
val hero = Dungeon.hero ?: return
// or
Dungeon.hero?.let { hero ->
    // use hero safely
}

// Bag.owner is Char? (nullable)
if (bag.owner?.isAlive == true) { ... }
```

Common nullable properties:
- `Dungeon.hero: Hero?`
- `Dungeon.level: Level?`
- `Bag.owner: Char?`
- `Item.curUser: Hero?`

### Property Access vs Methods

Kotlin converts Java getters to properties. Check the actual Kotlin API:

```kotlin
// Properties (no parentheses)
item.isIdentified      // NOT isIdentified()
item.isUpgradable      // NOT isUpgradable()
item.levelKnown        // NOT levelKnown()
item.cursedKnown       // NOT cursedKnown()

// Methods (with parentheses)
item.name()            // Returns String?
item.price()           // Returns Int
item.durability()      // Returns Float
item.maxDurability()   // Returns Float
```

### Type Conversions

GameMath and similar utilities use Float parameters:

```kotlin
// CORRECT
GameMath.gate(0f, value.toFloat(), max.toFloat())

// WRONG - Int parameters
GameMath.gate(0, value, max)  // Compilation error
```

### Color Constants

Use `.toInt()` for hex color literals:

```kotlin
private const val NORMAL = 0xFF4A4D44.toInt()
private const val EQUIPPED = 0xFF63665B.toInt()
```

## UI Component Patterns

### Visual Hierarchy in createChildren()

Background elements must be added BEFORE foreground elements:

```kotlin
override fun createChildren() {
    // 1. Add background FIRST
    bg = ColorBlock(width, height, color)
    add(bg)

    // 2. THEN call super (adds icons, labels)
    super.createChildren()
}
```

### Layout Positioning

Always override `layout()` to position visual elements:

```kotlin
override fun layout() {
    bg.x = x
    bg.y = y
    super.layout()
}
```

### ItemSlot Pattern

When extending `ItemSlot`, implement these methods:
- `createChildren()` - Create background before super call
- `layout()` - Position background at (x, y)
- `item(item: Item?)` - Configure visuals based on item state
- `onTouchDown()` / `onTouchUp()` - Touch feedback
- `onClick()` - Click handler
- `onLongClick()` - Long-press handler

## Common Pitfalls

1. **Hero null checks**: Always check `Dungeon.hero` before use
2. **Object singletons**: No `.INSTANCE` - call methods directly
3. **Float vs Int**: Check parameter types in utility methods
4. **createChildren order**: Background before super.createChildren()
5. **Missing layout()**: Positions elements correctly when window resizes

## Key Entry Points

| File | Purpose |
|------|---------|
| `PixelDungeon.kt` | Application entry point |
| `Game.kt` | Core engine with game loop |
| `Dungeon.kt` | Game state singleton |
| `GameScene.kt` | Main gameplay scene |
| `Hero.kt` | Player character |

## Skills

The `/kotlin-migrate` skill is available for Java-to-Kotlin migration cleanup tasks.
