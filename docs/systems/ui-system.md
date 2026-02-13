# UI System

This document describes the user interface system in Amazing Pixel Dungeon.

## Component Hierarchy

```
Gizmo
  └── Visual
        └── Group
              └── Component          # Base UI component
                    ├── Button       # Clickable button
                    │     ├── RedButton
                    │     ├── SimpleButton
                    │     └── ItemSlot
                    ├── ScrollPane   # Scrollable container
                    └── Window       # Modal dialog
```

## Base Component

### Component.kt
**Path**: `noosa/ui/Component.kt`

The foundation for all UI elements:

```kotlin
open class Component : Group() {
    protected var x: Float = 0f
    protected var y: Float = 0f
    protected var width: Float = 0f
    protected var height: Float = 0f

    fun setPos(x: Float, y: Float) {
        this.x = x
        this.y = y
        layout()
    }

    fun setSize(width: Float, height: Float) {
        this.width = width
        this.height = height
        layout()
    }

    open fun createChildren() { }
    open fun layout() { }

    override fun create() {
        createChildren()
        layout()
    }
}
```

### Component Initialization Order (Kotlin Conversion Pitfall)

The `Component()` constructor calls `createChildren()` **before** subclass property initializers and `init` blocks run. This is a critical difference from Java, where field initializers run before the constructor body.

**Kotlin initialization order for `class Foo(val param: Type) : Component()`:**

```
1. Component() constructor runs
   └── calls createChildren()    ← subclass override runs HERE
   └── calls layout()
2. Subclass property initializers run  (val x = ..., var y: Type? = null)
3. Subclass init {} blocks run
```

This means:

- **Constructor parameter properties** (`val`/`var` params) are **null/0** inside `createChildren()` — their backing fields are not yet assigned.
- **Property initializers** like `var image: Image? = null` run AFTER `createChildren()` and will **reset** values that `createChildren()` set.
- **`init {}` blocks** run AFTER both `createChildren()` and property initializers, so they can safely access both.

#### Bug Pattern 1: Constructor param accessed in createChildren()

```kotlin
// WRONG — item is null when createChildren() runs
class ItemButton(protected var item: Item) : Button() {
    override fun createChildren() {
        slot = ItemSlot()
        add(slot)
        slot.item(item)         // NPE! item backing field not assigned yet
        if (item.cursed) { ... } // NPE!
    }
}

// CORRECT — create widgets in createChildren(), configure in init
class ItemButton(protected var item: Item) : Button() {
    override fun createChildren() {
        slot = ItemSlot()
        add(slot)
    }
    init {
        slot.item(item)         // item is assigned by now
        if (item.cursed) { ... } // safe
    }
}
```

#### Bug Pattern 2: Nullable initializer resets createChildren() value

```kotlin
// WRONG — "= null" initializer runs AFTER createChildren(), resetting the value
class ChallengeButton : Button() {
    private var image: Image? = null  // resets to null after createChildren!
    override fun createChildren() {
        image = Icons.get(Icons.CHALLENGE_ON)  // set here...
        add(image!!)
    }
    init {
        width = image!!.width  // NPE! image was reset to null
    }
}

// CORRECT — use lateinit (no initializer to reset)
class ChallengeButton : Button() {
    private lateinit var image: Image
    override fun createChildren() {
        image = Icons.get(Icons.CHALLENGE_ON)
        add(image)
    }
    init {
        width = image.width  // safe — lateinit has no resetting initializer
    }
}
```

#### Safe Pattern Summary

| Where | Constructor params | lateinit properties from createChildren() |
|-------|-------------------|------------------------------------------|
| `createChildren()` | **NOT available** (null/0) | Being created here |
| Property initializers | Available | Available but may **reset** values — use `lateinit` instead |
| `init {}` blocks | Available | Available |
| `layout()` (called later) | Available | Available |

**Rule**: In `createChildren()`, only create widgets and add them. Move all configuration that depends on constructor parameters or widget values to an `init {}` block. Always use `lateinit var` (not `var x: Type? = null`) for properties assigned in `createChildren()`.

**Fixed instances of this bug:**
- `WndRanking.ItemButton` — `item` param accessed in `createChildren()`
- `WndRanking.LabelledItemButton` — inherits from ItemButton
- `WndJournal.ListItem` — `featureDesc`/`depthVal` params accessed in `createChildren()`
- `BadgesList.ListItem` — `badge` param accessed in `createChildren()`
- `StartScene.ClassShield` — `var avatar: Image? = null` reset value from `createChildren()`
- `StartScene.ChallengeButton` — `var image: Image? = null` reset value from `createChildren()`

## Input Handling

### Signal/Listener Pattern

The UI uses a signal-based event system:

```kotlin
class Signal<T> {
    private val listeners = ArrayList<Listener<T>>()

    fun add(listener: Listener<T>): Listener<T> {
        listeners.add(listener)
        return listener
    }

    fun remove(listener: Listener<T>) {
        listeners.remove(listener)
    }

    fun dispatch(value: T) {
        listeners.forEach { it.onSignal(value) }
    }
}

interface Listener<T> {
    fun onSignal(value: T): Boolean
}
```

### TouchArea
Handles touch/click events on a region:

```kotlin
class TouchArea(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) : Gizmo(), Listener<MotionEvent> {

    val onClick = Signal<TouchArea>()

    fun contains(x: Float, y: Float): Boolean {
        return x >= this.x && x < this.x + width &&
               y >= this.y && y < this.y + height
    }

    override fun onSignal(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (contains(event.x, event.y)) {
                    onTouchDown(event)
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                onClick.dispatch(this)
                onTouchUp(event)
            }
        }
        return false
    }

    open fun onTouchDown(event: MotionEvent) { }
    open fun onTouchUp(event: MotionEvent) { }
}
```

### PointerArea
Enhanced touch area with hover states:

```kotlin
class PointerArea(target: Visual) : TouchArea(
    target.x, target.y, target.width, target.height
) {
    var target: Visual = target

    var hovered: Boolean = false
    var pressed: Boolean = false

    override fun onTouchDown(event: MotionEvent) {
        pressed = true
        target.brightness(1.2f)
    }

    override fun onTouchUp(event: MotionEvent) {
        pressed = false
        target.brightness(1f)
    }
}
```

## Button Components

### Button.kt
Base button with touch states:

```kotlin
abstract class Button : Component() {
    protected var hotArea: PointerArea? = null

    override fun createChildren() {
        hotArea = PointerArea(this).apply {
            onClick.add { onClick() }
        }
        add(hotArea!!)
    }

    protected open fun onClick() { }
    protected open fun onTouchDown() { }
    protected open fun onTouchUp() { }
}
```

### RedButton.kt
**Path**: `ui/RedButton.kt`

The primary styled button:

```kotlin
class RedButton(label: String) : Button() {
    private lateinit var bg: NinePatch
    private lateinit var text: BitmapText

    override fun createChildren() {
        super.createChildren()

        bg = Chrome.get(Chrome.Type.BUTTON)
        add(bg)

        text = PixelScene.createText(label, 9f)
        add(text)
    }

    override fun layout() {
        bg.x = x
        bg.y = y
        bg.size(width, height)

        text.x = x + (width - text.width()) / 2
        text.y = y + (height - text.height()) / 2
    }

    fun text(value: String) {
        text.text(value)
        layout()
    }

    fun enable(value: Boolean) {
        active = value
        text.alpha(if (value) 1f else 0.3f)
    }
}
```

### CheckBox.kt
**Path**: `ui/CheckBox.kt`

Toggle checkbox:

```kotlin
class CheckBox(label: String) : RedButton(label) {
    private lateinit var checkIcon: Image
    var checked: Boolean = false

    override fun createChildren() {
        super.createChildren()

        checkIcon = Icons.get(Icons.CHECKED)
        add(checkIcon)
        checkIcon.visible = checked
    }

    override fun onClick() {
        checked = !checked
        checkIcon.visible = checked
    }
}
```

## Inventory UI

### ItemSlot.kt
**Path**: `ui/ItemSlot.kt`

Displays a single item:

```kotlin
open class ItemSlot(item: Item? = null) : Button() {
    protected lateinit var icon: ItemSprite
    protected lateinit var topLeft: BitmapText
    protected lateinit var topRight: BitmapText
    protected lateinit var bottomRight: BitmapText

    var item: Item? = item
        set(value) {
            field = value
            updateSlot()
        }

    override fun createChildren() {
        super.createChildren()

        icon = ItemSprite()
        add(icon)

        topLeft = BitmapText(PixelScene.pixelFont)
        add(topLeft)

        topRight = BitmapText(PixelScene.pixelFont)
        add(topRight)

        bottomRight = BitmapText(PixelScene.pixelFont)
        add(bottomRight)
    }

    open fun updateSlot() {
        item?.let { item ->
            icon.view(item)

            topLeft.text(item.status())
            topRight.text(item.topRightStatus())
            bottomRight.text(item.bottomRightStatus())

            if (item.cursed && item.cursedKnown) {
                icon.alpha(0.6f)
            }
        } ?: run {
            icon.view(ItemSpriteSheet.SOMETHING, null)
            topLeft.text(null)
            topRight.text(null)
            bottomRight.text(null)
        }
    }
}
```

### QuickSlot.kt
**Path**: `ui/QuickSlot.kt`

Quick access inventory slot:

```kotlin
class QuickSlot : ItemSlot() {
    companion object {
        var primaryValue: Item? = null
        var secondaryValue: Item? = null
    }

    override fun onClick() {
        item?.let { item ->
            GameScene.selectCell(object : CellSelector.Listener {
                override fun onSelect(cell: Int?) {
                    cell?.let { item.use(Dungeon.hero!!, it) }
                }
            })
        }
    }

    override fun onLongClick(): Boolean {
        item?.let {
            GameScene.scene?.addWindow(WndBag(
                Dungeon.hero!!.belongings.backpack,
                null,
                WndBag.Mode.QUICKSLOT,
                null
            ))
            return true
        }
        return false
    }
}
```

## HUD Elements

### StatusPane.kt
**Path**: `ui/StatusPane.kt`

Hero status display at top of screen:

```kotlin
class StatusPane : Component() {
    private lateinit var bg: NinePatch
    private lateinit var avatar: Image
    private lateinit var hp: HealthBar
    private lateinit var exp: ExpBar
    private lateinit var level: BitmapText
    private lateinit var depth: BitmapText
    private lateinit var danger: DangerIndicator
    private lateinit var buffs: BuffIndicator

    override fun createChildren() {
        bg = NinePatch(Assets.STATUS, 80, 0, 30, 0)
        add(bg)

        avatar = HeroSprite.avatar(Dungeon.hero!!.heroClass)
        add(avatar)

        hp = HealthBar()
        add(hp)

        exp = ExpBar()
        add(exp)

        level = BitmapText(PixelScene.pixelFont)
        add(level)

        depth = BitmapText(Integer.toString(Dungeon.depth), PixelScene.pixelFont)
        add(depth)

        danger = DangerIndicator()
        add(danger)

        buffs = BuffIndicator(Dungeon.hero!!)
        add(buffs)
    }

    override fun update() {
        super.update()

        val hero = Dungeon.hero!!

        hp.level(hero.HP, hero.HT)
        exp.level(hero.exp, hero.maxExp())

        if (danger.visible) {
            danger.update()
        }
    }
}
```

### HealthBar.kt
**Path**: `ui/HealthBar.kt`

Visual health bar:

```kotlin
class HealthBar : Component() {
    private lateinit var bg: ColorBlock
    private lateinit var health: ColorBlock

    override fun createChildren() {
        bg = ColorBlock(1f, 1f, 0xFF4C2B19.toInt())
        add(bg)

        health = ColorBlock(1f, 1f, 0xFF40B040.toInt())
        add(health)
    }

    fun level(current: Int, max: Int) {
        val ratio = current.toFloat() / max.toFloat()
        health.size(width * ratio, height)

        // Color changes based on health
        health.color(when {
            ratio > 0.5f -> 0xFF40B040.toInt()  // Green
            ratio > 0.25f -> 0xFFFFFF00.toInt() // Yellow
            else -> 0xFFFF0000.toInt()          // Red
        })
    }
}
```

### BuffIndicator.kt
**Path**: `ui/BuffIndicator.kt`

Shows active status effects:

```kotlin
class BuffIndicator(private val ch: Char) : Component() {
    private val icons = LinkedHashMap<Int, Image>()

    override fun update() {
        super.update()

        // Check for new buffs
        ch.buffs.forEach { buff ->
            val icon = buff.icon()
            if (icon != BuffIndicator.NONE && !icons.containsKey(icon)) {
                val img = Image(Assets.BUFFS)
                img.frame(TextureFilm(Assets.BUFFS, 7, 8).get(icon))
                icons[icon] = img
                add(img)
                layout()
            }
        }

        // Remove expired buffs
        icons.keys.filter { key ->
            ch.buffs.none { it.icon() == key }
        }.forEach { key ->
            icons.remove(key)?.destroy()
            layout()
        }
    }

    override fun layout() {
        var pos = 0f
        icons.values.forEach { icon ->
            icon.x = x + pos
            icon.y = y
            pos += icon.width + 2
        }
    }
}
```

### Toolbar.kt
**Path**: `ui/Toolbar.kt`

Bottom action toolbar:

```kotlin
class Toolbar : Component() {
    private lateinit var btnWait: Tool
    private lateinit var btnSearch: Tool
    private lateinit var btnInventory: Tool
    private lateinit var btnQuick: QuickSlot

    override fun createChildren() {
        btnWait = object : Tool(24, 32, 20, 24) {
            override fun onClick() {
                Dungeon.hero!!.rest(false)
            }
        }
        add(btnWait)

        btnSearch = object : Tool(44, 32, 20, 24) {
            override fun onClick() {
                Dungeon.hero!!.search(true)
            }
        }
        add(btnSearch)

        btnInventory = object : Tool(64, 32, 20, 24) {
            override fun onClick() {
                GameScene.scene?.addWindow(
                    WndBag(Dungeon.hero!!.belongings.backpack, null)
                )
            }
        }
        add(btnInventory)

        btnQuick = QuickSlot()
        add(btnQuick)
    }
}
```

### GameLog.kt
**Path**: `ui/GameLog.kt`

Combat and event messages:

```kotlin
class GameLog : Component() {
    companion object {
        const val MAX_LINES = 3

        fun add(text: String, vararg args: Any) {
            scene?.log?.add(text.format(*args))
        }
    }

    private val lines = ArrayList<BitmapTextMultiline>()

    fun add(text: String) {
        val line = PixelScene.createMultiline(text, 6f)
        line.maxWidth = width.toInt()
        add(line)
        lines.add(line)

        // Remove old lines
        while (lines.size > MAX_LINES) {
            val old = lines.removeAt(0)
            old.destroy()
        }

        layout()
    }
}
```

## Window System

### Window.kt
**Path**: `ui/Window.kt`

Base modal dialog:

```kotlin
open class Window(
    width: Int = 0,
    height: Int = 0
) : Group() {
    protected lateinit var chrome: NinePatch

    companion object {
        const val TITLE_COLOR = 0xFFFF44
    }

    var width: Float = width.toFloat()
    var height: Float = height.toFloat()

    init {
        chrome = Chrome.get(Chrome.Type.WINDOW)
        chrome.size(this.width + chrome.marginHor(),
                    this.height + chrome.marginVer())
        add(chrome)
    }

    fun resize(w: Int, h: Int) {
        width = w.toFloat()
        height = h.toFloat()
        chrome.size(width + chrome.marginHor(),
                    height + chrome.marginVer())
    }

    open fun hide() {
        parent?.let { parent ->
            parent.erase(parent.blocker)
            parent.erase(this)
        }
    }

    protected fun add(child: Gizmo): Gizmo {
        child.x += chrome.marginLeft()
        child.y += chrome.marginTop()
        return super.add(child)
    }
}
```

### Common Window Types

| Window | Purpose |
|--------|---------|
| `WndBag` | Inventory management |
| `WndItem` | Item details and actions |
| `WndHero` | Character sheet |
| `WndInfoMob` | Enemy information |
| `WndInfoCell` | Terrain information |
| `WndInfoPlant` | Plant details |
| `WndSettings` | Game settings |
| `WndGame` | Pause menu |
| `WndMessage` | Simple message |
| `WndTitledMessage` | Message with title |
| `WndOptions` | Multiple choice |
| `WndTradeItem` | Shop buy/sell |
| `WndBlacksmith` | Forge reroll |
| `WndWandmaker` | Wand quest |
| `WndJournal` | Quest log |
| `WndCatalogus` | Item catalog |
| `WndResurrect` | Ankh revival prompt |
| `WndCrafting` | Generic crafting recipe list (by station type) |
| `WndFurnace` | Furnace smelting recipes |
| `WndEnchanting` | Enchanting weapons + recipes button |
| `WndAnvil` | Anvil repair and enchanted book application |
| `WndStorageChest` | Storage/dimensional chest item transfer |

### WndBag.kt
**Path**: `windows/WndBag.kt`

Inventory window:

```kotlin
class WndBag(
    bag: Bag,
    listener: Listener?,
    mode: Mode = Mode.ALL,
    title: String?
) : Window() {

    enum class Mode { ALL, UNIDENTIFIED, UPGRADEABLE, QUICKSLOT, FOR_SALE, WEAPON, ARMOR, ENCHANTABLE, WAND, SEED, SCROLL, BOOK }

    interface Listener {
        fun onSelect(item: Item?)
    }

    private val COLS = 4
    private val SLOT_SIZE = 28

    override fun createChildren() {
        // Title
        title?.let { text ->
            val titleText = PixelScene.createText(text, 9f)
            titleText.hardlight(TITLE_COLOR)
            add(titleText)
        }

        // Item grid
        var pos = 0
        bag.forEach { item ->
            val slot = object : ItemSlot(item) {
                override fun onClick() {
                    if (mode.accepts(item)) {
                        hide()
                        listener?.onSelect(item)
                    }
                }
            }
            slot.setPos((pos % COLS) * SLOT_SIZE, (pos / COLS) * SLOT_SIZE)
            add(slot)
            pos++
        }

        // Tab buttons for different bags
        addTabs()
    }
}
```

## ScrollPane

### ScrollPane.kt
**Path**: `ui/ScrollPane.kt`

Scrollable content container that uses a **separate Camera** for its content, enabling viewport clipping and scroll offset.

**How it works:**
1. In `init`, ScrollPane creates a tiny 1x1 content camera: `content.camera = Camera(0, 0, 1, 1, zoom)`
2. In `layout()`, the content camera is repositioned and resized to match the ScrollPane's bounds
3. Scrolling is achieved by adjusting `content.camera.scroll`

**Usage pattern** (see `WndJournal` for reference):
```kotlin
val content = Component()
// ... add children to content, track vertical position ...
content.setSize(WIDTH.toFloat(), pos)

val list = ScrollPane(content)
add(list)
list.setRect(0f, titleHeight, WIDTH.toFloat(), height - titleHeight)
```

**Important**: ScrollPane's `layout()` uses `camera()` (the method) to find the parent Window's camera via hierarchy traversal. See [Camera Property vs Method](rendering-system.md#camera-property-vs-method-kotlin-conversion-pitfall) for details on why this distinction matters. If `camera` (the property) were used instead, it would be null — causing layout to exit early and the content camera to remain at 1x1 pixels, making all content invisible.

**Click dispatch**: ScrollPane's `TouchController` intercepts all touch events in the scroll region. On a non-drag click it calls `ScrollPane.onClick(x, y)` with content-space coordinates, but the **default implementation is empty**. Buttons inside the ScrollPane content never receive touch events directly. To handle clicks, override `onClick` on an anonymous subclass and manually hit-test children:

```kotlin
val list = object : ScrollPane(content) {
    override fun onClick(x: Float, y: Float) {
        for (slot in slots) {
            if (slot.handleClick(x, y)) break  // public method using inside(x, y)
        }
    }
}
```

See `WndCrafting`, `WndFurnace`, `WndStorageChest`, and `WndCatalogus` for examples of this pattern.

## Toast Notifications

### Toast.kt
**Path**: `ui/Toast.kt`

Temporary popup messages:

```kotlin
class Toast(text: String) : Component() {
    private lateinit var bg: NinePatch
    private lateinit var message: BitmapText

    var lifespan: Float = 3f

    override fun createChildren() {
        bg = Chrome.get(Chrome.Type.TOAST)
        add(bg)

        message = PixelScene.createText(text, 8f)
        add(message)
    }

    override fun update() {
        super.update()

        lifespan -= Game.elapsed
        if (lifespan <= 0) {
            destroy()
        } else if (lifespan < 1) {
            alpha(lifespan)
        }
    }
}
```

## Chrome Styling

### Chrome.kt
**Path**: `Chrome.kt`

UI styling with nine-patch backgrounds:

```kotlin
object Chrome {
    enum class Type {
        WINDOW,
        BUTTON,
        TOAST,
        TAB_SET,
        TAB_SELECTED,
        TAB_UNSELECTED,
        SCROLL,
        INPUT,
        TAG
    }

    fun get(type: Type): NinePatch {
        return when (type) {
            Type.WINDOW -> NinePatch(Assets.CHROME, 0, 0, 20, 20)
            Type.BUTTON -> NinePatch(Assets.CHROME, 20, 0, 6, 6)
            Type.TOAST -> NinePatch(Assets.CHROME, 40, 0, 6, 6)
            // ... etc
        }
    }
}
```

## See Also

- [Scene System](scene-system.md) - Scene management
- [Rendering System](rendering-system.md) - Visual rendering
- [Architecture Overview](../architecture/overview.md) - System architecture
