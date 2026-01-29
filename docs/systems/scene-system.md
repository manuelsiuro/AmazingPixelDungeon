# Scene System

This document describes the scene management system in Amazing Pixel Dungeon.

## Scene Hierarchy

```
Gizmo                    # Base scene graph node
  └── Visual             # Renderable with position/size
        └── Group        # Container for children
              └── Scene  # Root of scene graph
                    └── PixelScene  # Game-specific base
                          └── [Specific Scenes]
```

## Base Classes

### Gizmo
The fundamental building block of the scene graph:

```kotlin
abstract class Gizmo {
    var parent: Group? = null
    var exists: Boolean = true
    var active: Boolean = true
    var visible: Boolean = true

    open fun update() { }
    open fun destroy() { }
    fun remove() { parent?.remove(this) }
    fun kill() { exists = false }
    fun revive() { exists = true }
}
```

### Visual
Adds transform properties:

```kotlin
abstract class Visual : Gizmo() {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    var scale: PointF = PointF(1f, 1f)
    var origin: PointF = PointF()
    var angle: Float = 0f

    var speed: PointF = PointF()
    var acc: PointF = PointF()

    open fun draw() { }

    fun point(): PointF = PointF(x, y)
    fun center(): PointF = PointF(x + width / 2, y + height / 2)
}
```

### Group
Container that manages child visuals:

```kotlin
open class Group : Visual() {
    protected val members = ArrayList<Gizmo>()

    open fun add(g: Gizmo): Gizmo {
        g.parent?.remove(g)
        g.parent = this
        members.add(g)
        return g
    }

    open fun remove(g: Gizmo): Gizmo? {
        if (members.remove(g)) {
            g.parent = null
            return g
        }
        return null
    }

    override fun update() {
        members.forEach { if (it.exists && it.active) it.update() }
    }

    override fun draw() {
        members.forEach { if (it.exists && it.visible) it.draw() }
    }

    fun clear() {
        members.toList().forEach { it.destroy() }
        members.clear()
    }
}
```

### Scene
The root of a scene graph with lifecycle:

```kotlin
abstract class Scene : Group() {
    open fun create() { }
    open fun destroy() { }
    open fun pause() { }
    open fun resume() { }

    open fun onBackPressed() { Game.instance.finish() }
    open fun onMenuPressed() { }
}
```

## PixelScene Base

All game scenes extend `PixelScene`:

```kotlin
abstract class PixelScene : Scene() {
    companion object {
        const val MIN_WIDTH_P = 128f
        const val MIN_HEIGHT_P = 224f
        const val MIN_WIDTH_L = 224f
        const val MIN_HEIGHT_L = 160f

        var defaultZoom: Float = 0f
        var minZoom: Float = 0f
        var maxZoom: Float = 0f

        var uiCamera: Camera? = null
    }

    override fun create() {
        super.create()
        // Calculate pixel-perfect scaling
        // Set up UI camera
        // Add background decorations
    }

    protected fun add(window: Window) {
        // Modal window management
    }

    protected fun alignToPixels(v: Visual) {
        v.x = floor(v.x)
        v.y = floor(v.y)
    }
}
```

## Lifecycle Methods

### Scene Lifecycle Flow

```
┌─────────────────────────────────────────────────────┐
│                    create()                          │
│  - Initialize scene components                       │
│  - Set up cameras                                    │
│  - Create UI elements                                │
│  - Add visual children                               │
└─────────────────────────┬───────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│                    resume()                          │
│  - Called on scene activation                        │
│  - Resume audio/animations                           │
└─────────────────────────┬───────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│              update() / draw() loop                  │
│  - Called every frame (~30 FPS)                      │
│  - update(): Logic and state                         │
│  - draw(): Rendering                                 │
└─────────────────────────┬───────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│                    pause()                           │
│  - App backgrounded or scene changing                │
│  - Save state if needed                              │
│  - Pause audio                                       │
└─────────────────────────┬───────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────┐
│                    destroy()                         │
│  - Clean up resources                                │
│  - Remove listeners                                  │
│  - Clear children                                    │
└─────────────────────────────────────────────────────┘
```

## Scene Types

### TitleScene
**Path**: `scenes/TitleScene.kt`

The main menu shown on game launch.

**Components**:
- Game logo/banner
- Touch prompt
- Version text
- Background decorations

**Transitions**:
- Touch → `StartScene`
- Settings button → `WndSettings`

### StartScene
**Path**: `scenes/StartScene.kt`

Character class selection and game start.

**Components**:
- Hero class buttons (Warrior, Mage, Rogue, Huntress)
- Class info panel
- Badges display
- Challenge selector
- Continue game option

**Transitions**:
- Select class → `InterlevelScene` → `GameScene`
- Info button → `WndClass`

### InterlevelScene
**Path**: `scenes/InterlevelScene.kt`

Loading/transition screen between levels.

```kotlin
class InterlevelScene : PixelScene() {
    enum class Mode { DESCEND, ASCEND, CONTINUE, RESURRECT, RETURN, FALL }

    companion object {
        var mode: Mode = Mode.DESCEND
        var returnDepth: Int = 0
        var returnPos: Int = -1
    }

    override fun create() {
        // Show loading text
        // Execute level transition in background
        thread = Thread {
            when (mode) {
                Mode.DESCEND -> descend()
                Mode.ASCEND -> ascend()
                Mode.CONTINUE -> restore()
                Mode.RESURRECT -> resurrect()
                Mode.RETURN -> returnTo()
                Mode.FALL -> fall()
            }
        }
        thread.start()
    }
}
```

**Modes**:
| Mode | Trigger | Action |
|------|---------|--------|
| DESCEND | Stairs down | Generate/load next level |
| ASCEND | Stairs up | Load previous level |
| CONTINUE | Load game | Restore saved state |
| RESURRECT | Ankh use | Respawn hero |
| RETURN | Lloyd's Beacon | Return to marked location |
| FALL | Chasm | Generate level, take damage |

### GameScene
**Path**: `scenes/GameScene.kt`

The main gameplay scene with dungeon rendering.

**Visual Layers** (bottom to top):
```kotlin
class GameScene : PixelScene() {
    companion object {
        var scene: GameScene? = null
    }

    lateinit var terrain: DungeonTerrainTilemap
    lateinit var customTiles: DungeonTilemap
    lateinit var levelVisuals: Group
    lateinit var heaps: Group
    lateinit var mobs: Group
    lateinit var emitters: Group
    lateinit var effects: Group
    lateinit var gases: Group
    lateinit var spells: Group
    lateinit var statuses: Group
    lateinit var healthIndicators: Group
    lateinit var hero: HeroSprite
    lateinit var fog: FogOfWar

    lateinit var pane: StatusPane
    lateinit var toolbar: Toolbar
    lateinit var attack: AttackIndicator
    lateinit var log: GameLog
}
```

**Layer Order**:
1. `terrain` - Floor/wall tiles
2. `customTiles` - Level-specific decorations
3. `levelVisuals` - Water, grass effects
4. `heaps` - Items on ground
5. `mobs` - Enemy sprites
6. `hero` - Player sprite
7. `emitters` - Particle effects
8. `effects` - Animations (missiles, etc.)
9. `gases` - Blob visuals
10. `fog` - Fog of war overlay
11. UI elements (pane, toolbar, log)

**Key Methods**:
```kotlin
fun addMobSprite(mob: Mob): CharSprite
fun addHeapSprite(heap: Heap): HeapSprite
fun addBlobSprite(blob: Blob): BlobEmitter
fun addPlantSprite(plant: Plant): PlantSprite

fun updateMap()              // Refresh tiles
fun updateMap(cell: Int)     // Refresh single cell
fun discover(cell: Int)      // Reveal cell

// Cell selection
fun selectCell(listener: CellSelector.Listener)
```

### SurfaceScene
**Path**: `scenes/SurfaceScene.kt`

Victory scene when hero escapes with Amulet.

**Components**:
- Animated surface background
- Hero sprite walking away
- Victory text
- Rankings access

### AmuletScene
**Path**: `scenes/AmuletScene.kt`

Endgame choice scene upon obtaining Amulet.

**Choices**:
1. Stay and fight (continue to floor 26)
2. Ascend to surface (victory path)

### RankingsScene
**Path**: `scenes/RankingsScene.kt`

High score display.

**Components**:
- Top 10 runs
- Score breakdown
- Hero class icon
- Death cause / victory status

### BadgesScene
**Path**: `scenes/BadgesScene.kt`

Achievement gallery.

**Components**:
- Badge grid
- Category tabs
- Unlock status
- Badge info popup

### AboutScene
**Path**: `scenes/AboutScene.kt`

Credits and game information.

## Scene Transitions

### Switching Scenes

```kotlin
// In Game.kt
fun switchScene(sceneClass: Class<out Scene>) {
    scene?.pause()
    scene?.destroy()

    scene = sceneClass.newInstance()
    scene?.create()
    scene?.resume()
}

// Usage
Game.switchScene(StartScene::class.java)
```

### Transition Pattern

```kotlin
// Example: Starting a new game
class StartScene : PixelScene() {
    fun startNewGame(heroClass: HeroClass) {
        // Save selection
        Dungeon.hero = Hero(heroClass)

        // Set transition mode
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND

        // Switch to loading scene
        Game.switchScene(InterlevelScene::class.java)
    }
}
```

## Scene Flow Diagram

```
                    ┌─────────────┐
                    │ TitleScene  │
                    └──────┬──────┘
                           │ touch
                           ▼
                    ┌─────────────┐
                    │ StartScene  │◄──────────────┐
                    └──────┬──────┘               │
                           │ select class         │
                           ▼                      │
                    ┌─────────────┐               │
                    │ Interlevel  │               │
                    │   Scene     │               │
                    └──────┬──────┘               │
                           │ load complete        │
                           ▼                      │
              ┌───────────────────────────┐       │
              │                           │       │
              │        GameScene          │       │
              │                           │       │
              │  ┌─────────────────────┐  │       │
              │  │   Dungeon Play      │  │       │
              │  │   - Combat          │  │       │
              │  │   - Exploration     │  │       │
              │  │   - Level change    │◀─┼───────┤
              │  └─────────────────────┘  │       │
              │                           │       │
              └──────┬──────────┬─────────┘       │
                     │          │                 │
           hero dies │          │ get Amulet     │
                     ▼          ▼                 │
              ┌──────────┐ ┌──────────┐           │
              │ Rankings │ │  Amulet  │           │
              │  Scene   │ │  Scene   │           │
              └──────────┘ └────┬─────┘           │
                                │ escape          │
                                ▼                 │
                         ┌──────────┐             │
                         │ Surface  │             │
                         │  Scene   │             │
                         └────┬─────┘             │
                              │                   │
                              └───────────────────┘
                                    replay
```

## Window Management

Windows are modal dialogs displayed over scenes:

```kotlin
// In PixelScene
protected fun add(window: Window) {
    // Darken background
    blocker = ColorBlock(width, height, 0x80000000)
    add(blocker)

    // Center window
    window.x = (width - window.width) / 2
    window.y = (height - window.height) / 2
    add(window)
}

// In Window
fun hide() {
    parent?.let { parent ->
        parent.erase(blocker)
        parent.erase(this)
    }
}
```

## Camera System

Each scene can have multiple cameras:

```kotlin
class Camera {
    companion object {
        var main: Camera? = null  // World camera
    }

    var x: Float = 0f
    var y: Float = 0f
    var zoom: Float = 1f

    var scroll: PointF = PointF()
    var target: Visual? = null

    fun follow(target: Visual, lerp: Float) {
        this.target = target
        // Smooth camera follow
    }

    fun shake(magnitude: Float, duration: Float) {
        // Screen shake effect
    }
}
```

## See Also

- [UI System](ui-system.md) - UI component details
- [Rendering System](rendering-system.md) - Sprite and animation
- [Architecture Overview](../architecture/overview.md) - System architecture
