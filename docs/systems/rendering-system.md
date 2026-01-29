# Rendering System

This document describes the Noosa 2D rendering engine used in Amazing Pixel Dungeon.

## Noosa Engine Overview

Noosa is a custom 2D rendering framework built on OpenGL ES 2.0 for Android. It provides:

- Scene graph management
- Sprite batching
- Texture atlas support
- Animation system
- Particle effects
- Camera control

## Architecture

```
┌───────────────────────────────────────────────────────┐
│                    Game.draw()                         │
├───────────────────────────────────────────────────────┤
│                     Scene                              │
│  ┌─────────────────────────────────────────────────┐  │
│  │               Visual Tree                        │  │
│  │  ┌────────┐ ┌────────┐ ┌────────┐ ┌──────────┐ │  │
│  │  │ Image  │ │ Sprite │ │Tilemap │ │ Emitter  │ │  │
│  │  └────────┘ └────────┘ └────────┘ └──────────┘ │  │
│  └─────────────────────────────────────────────────┘  │
├───────────────────────────────────────────────────────┤
│                   Camera                               │
│           (viewport, zoom, scroll)                     │
├───────────────────────────────────────────────────────┤
│                 NoosaScript                            │
│            (shader programs)                           │
├───────────────────────────────────────────────────────┤
│                OpenGL ES 2.0                           │
└───────────────────────────────────────────────────────┘
```

## Visual Classes

### Image
**Path**: `noosa/Image.kt`

Basic textured quad:

```kotlin
open class Image(texture: Any? = null) : Visual() {
    var texture: SmartTexture? = null
    protected var frame: RectF = RectF()

    var flipHorizontal: Boolean = false
    var flipVertical: Boolean = false

    init {
        texture?.let { texture(it) }
    }

    fun texture(src: Any) {
        texture = TextureCache.get(src)
        frame(RectF(0f, 0f, 1f, 1f))
    }

    fun frame(frame: RectF) {
        this.frame = frame
        width = frame.width() * texture!!.width
        height = frame.height() * texture!!.height
        updateFrame()
    }

    override fun draw() {
        super.draw()

        texture?.let { tex ->
            NoosaScript.get()
                .camera(camera)
                .uModel(matrix())
                .texture(tex)
                .lighting(
                    rm, gm, bm, am,
                    ra, ga, ba, aa
                )
                .drawQuad(vertices)
        }
    }
}
```

### MovieClip
**Path**: `noosa/MovieClip.kt`

Animated sprite with frame sequences:

```kotlin
open class MovieClip(texture: Any? = null) : Image(texture) {
    protected var curAnim: Animation? = null
    protected var curFrame: Int = 0
    protected var frameTimer: Float = 0f

    var paused: Boolean = false
    var listener: Listener? = null

    interface Listener {
        fun onComplete(anim: Animation)
    }

    fun play(anim: Animation, force: Boolean = false) {
        if (!force && curAnim == anim) return

        curAnim = anim
        curFrame = 0
        frameTimer = 0f

        frame(anim.frames[curFrame])
    }

    override fun update() {
        super.update()

        if (paused || curAnim == null) return

        frameTimer += Game.elapsed

        while (frameTimer >= curAnim!!.delay) {
            frameTimer -= curAnim!!.delay

            curFrame++
            if (curFrame >= curAnim!!.frames.size) {
                if (curAnim!!.looped) {
                    curFrame = 0
                } else {
                    curFrame = curAnim!!.frames.size - 1
                    paused = true
                    listener?.onComplete(curAnim!!)
                    break
                }
            }
            frame(curAnim!!.frames[curFrame])
        }
    }
}
```

### Animation
**Path**: `noosa/Animation.kt`

Frame sequence definition:

```kotlin
class Animation(val delay: Float, val looped: Boolean) {
    lateinit var frames: Array<RectF>

    fun frames(film: TextureFilm, vararg indices: Int): Animation {
        frames = indices.map { film.get(it) }.toTypedArray()
        return this
    }

    fun frames(film: TextureFilm, start: Int, end: Int): Animation {
        frames = (start..end).map { film.get(it) }.toTypedArray()
        return this
    }
}
```

### TextureFilm
**Path**: `noosa/TextureFilm.kt`

Sprite sheet slicer:

```kotlin
class TextureFilm(texture: Any, width: Int, height: Int = width) {
    private val texture: SmartTexture = TextureCache.get(texture)
    private val frames = HashMap<Int, RectF>()

    private val cols: Int
    private val rows: Int
    private val cellW: Float
    private val cellH: Float

    init {
        cols = this.texture.width / width
        rows = this.texture.height / height
        cellW = width.toFloat() / this.texture.width
        cellH = height.toFloat() / this.texture.height
    }

    fun get(index: Int): RectF {
        return frames.getOrPut(index) {
            val col = index % cols
            val row = index / cols
            RectF(
                col * cellW,
                row * cellH,
                (col + 1) * cellW,
                (row + 1) * cellH
            )
        }
    }

    fun get(row: Int, col: Int): RectF {
        return get(row * cols + col)
    }
}
```

## Sprite System

### CharSprite
**Path**: `sprites/CharSprite.kt`

Base character sprite with animations:

```kotlin
abstract class CharSprite : MovieClip(), MovieClip.Listener {
    companion object {
        const val DEFAULT = 0
        const val POSITIVE = 0x00FF00
        const val NEGATIVE = 0xFF0000
        const val WARNING = 0xFF8800
        const val NEUTRAL = 0xFFFF00
    }

    var ch: Char? = null

    // Animation states
    protected lateinit var idle: Animation
    protected lateinit var run: Animation
    protected lateinit var attack: Animation
    protected var die: Animation? = null

    // Visual feedback
    protected var flashTime: Float = 0f
    protected var sleeping: Boolean = false

    fun link(ch: Char) {
        this.ch = ch
        ch.sprite = this
        place(ch.pos)
        turnTo(ch.pos, Random.Int(Level.LENGTH))
    }

    fun place(cell: Int) {
        point(cell % Level.WIDTH * DungeonTilemap.SIZE.toFloat(),
              cell / Level.WIDTH * DungeonTilemap.SIZE.toFloat())
    }

    open fun idle() {
        play(idle)
    }

    open fun move(from: Int, to: Int) {
        play(run)

        val motion = PosTweener(this, cellToPoint(to), MOVE_INTERVAL)
        motion.listener = { idle() }
        parent?.add(motion)

        turnTo(from, to)
    }

    open fun attack(cell: Int) {
        turnTo(ch!!.pos, cell)
        play(attack)
    }

    open fun die() {
        die?.let { play(it) } ?: run {
            // Fade out
            parent?.add(AlphaTweener(this, 0f, DIE_INTERVAL).apply {
                listener = { remove() }
            })
        }
    }

    fun flash() {
        flashTime = FLASH_INTERVAL
        resetColor()
    }

    fun showStatus(color: Int, text: String) {
        val y = this.y - height / 2
        if (ch != null) {
            FloatingText.show(x + width / 2, y, ch!!.pos, text, color)
        }
    }

    override fun update() {
        super.update()

        if (flashTime > 0) {
            flashTime -= Game.elapsed
            if (flashTime <= 0) {
                resetColor()
            } else {
                // Flicker effect
                hardlight(0xFFFFFF)
            }
        }

        // Floating while levitating
        ch?.let { ch ->
            if (ch.flying) {
                y = ch.pos / Level.WIDTH * DungeonTilemap.SIZE - 4 +
                    (Math.sin(Game.timeTotal * 2.0) * 2).toFloat()
            }
        }
    }

    override fun onComplete(anim: Animation) {
        if (anim == attack) {
            idle()
        }
    }
}
```

### HeroSprite
**Path**: `sprites/HeroSprite.kt`

Player character sprite:

```kotlin
class HeroSprite : CharSprite() {
    companion object {
        private const val RUN_FRAMERATE = 20f

        fun avatar(heroClass: HeroClass): Image {
            val avatar = Image(heroClass.spritesheet)
            avatar.frame(TextureFilm(heroClass.spritesheet, 12).get(0))
            return avatar
        }
    }

    private lateinit var fly: Animation

    init {
        link(Dungeon.hero!!)

        val heroClass = (ch as Hero).heroClass
        texture(heroClass.spritesheet)
        val film = TextureFilm(texture, 12)

        idle = Animation(1f, true).frames(film, 0, 0, 0, 1, 0, 0, 1, 1)
        run = Animation(RUN_FRAMERATE, true).frames(film, 2, 3, 4, 5, 6, 7)
        die = Animation(20f, false).frames(film, 8, 9, 10, 11, 12, 11)
        attack = Animation(15f, false).frames(film, 13, 14, 15, 0)
        fly = Animation(1f, true).frames(film, 16, 17, 18, 19)

        play(idle)
    }

    override fun move(from: Int, to: Int) {
        super.move(from, to)

        // Camera follow
        Camera.main?.focusOn(this)
    }

    fun fly() {
        play(fly)
    }
}
```

### ItemSprite
**Path**: `sprites/ItemSprite.kt`

Item rendering sprite:

```kotlin
class ItemSprite(item: Item? = null) : MovieClip(Assets.ITEMS) {
    companion object {
        const val SIZE = 16
    }

    private val glowing = Glowing()

    init {
        item?.let { view(it) }
    }

    fun view(item: Item) {
        frame(ItemSpriteSheet.film.get(item.image))

        if (item.glowing != null) {
            glowing.set(item.glowing!!)
        } else {
            glowing.reset()
        }
    }

    fun view(image: Int, glowing: ItemSprite.Glowing?) {
        frame(ItemSpriteSheet.film.get(image))
        glowing?.let { this.glowing.set(it) } ?: this.glowing.reset()
    }

    override fun update() {
        super.update()
        if (glowing.active) {
            glowing.update()
            hardlight(glowing.color)
        }
    }

    class Glowing {
        var color: Int = 0xFFFFFF
        var active: Boolean = false
        var period: Float = 1f
        var phase: Float = 0f

        fun set(src: Glowing) {
            color = src.color
            active = true
            period = src.period
            phase = src.phase
        }

        fun reset() {
            active = false
        }

        fun update() {
            phase += Game.elapsed / period
            if (phase > 1) phase -= 1
            // Pulse brightness
            val alpha = 0.6f + 0.4f * sin(phase * 2 * PI).toFloat()
            // Apply alpha to color
        }
    }
}
```

### MissileSprite
**Path**: `sprites/MissileSprite.kt`

Projectile animation:

```kotlin
class MissileSprite : ItemSprite() {
    companion object {
        const val SPEED = 240f
    }

    private var callback: Callback? = null

    fun reset(from: Int, to: Int, item: Item?, callback: Callback?) {
        reset(from, to, item?.image ?: 0, item?.glowing, callback)
    }

    fun reset(from: Int, to: Int, image: Int, glowing: Glowing?, callback: Callback?) {
        this.callback = callback

        revive()
        view(image, glowing)

        // Start position
        val fromP = DungeonTilemap.tileToWorld(from)
        point(fromP.x, fromP.y)

        // Target position
        val toP = DungeonTilemap.tileToWorld(to)

        // Calculate angle
        val dx = toP.x - fromP.x
        val dy = toP.y - fromP.y
        angle = (atan2(dy, dx) * 180 / PI).toFloat() + 90

        // Calculate duration
        val distance = sqrt(dx * dx + dy * dy)
        val duration = distance / SPEED

        // Animate
        val tweener = PosTweener(this, toP, duration)
        tweener.listener = {
            kill()
            callback?.call()
        }
        parent?.add(tweener)
    }
}
```

## Tilemap Rendering

### Tilemap
**Path**: `noosa/Tilemap.kt`

Efficient tile-based rendering:

```kotlin
open class Tilemap(
    texture: Any,
    protected val tileset: TextureFilm
) : Visual() {
    protected var data: IntArray? = null
    protected var mapWidth: Int = 0
    protected var mapHeight: Int = 0
    protected var cellW: Int = 0
    protected var cellH: Int = 0

    protected var vertices: FloatArray? = null
    protected var dirty: Boolean = true

    fun map(data: IntArray, cols: Int) {
        this.data = data
        mapWidth = cols
        mapHeight = data.size / cols
        dirty = true
    }

    fun updateCell(cell: Int) {
        // Update single cell vertices
        val x = cell % mapWidth
        val y = cell / mapWidth
        val tile = data!![cell]

        if (tile >= 0) {
            val uv = tileset.get(tile)
            // Update vertex data for this cell
            updateVertices(x, y, uv)
        }
    }

    override fun draw() {
        if (dirty) {
            buildVertices()
            dirty = false
        }

        texture?.let { tex ->
            NoosaScript.get()
                .camera(camera)
                .uModel(matrix())
                .texture(tex)
                .drawQuads(vertices!!)
        }
    }
}
```

### DungeonTilemap
**Path**: `DungeonTilemap.kt`

Game-specific tilemap:

```kotlin
class DungeonTilemap : Tilemap(
    Dungeon.level!!.tilesTex(),
    TextureFilm(Dungeon.level!!.tilesTex(), SIZE, SIZE)
) {
    companion object {
        const val SIZE = 16
    }

    init {
        map(Dungeon.level!!.map, Level.WIDTH)
    }

    fun updateCell(cell: Int, tile: Int) {
        Dungeon.level!!.map[cell] = tile
        super.updateCell(cell)
    }

    // Convert cell index to world coordinates
    fun tileToWorld(cell: Int): PointF {
        return PointF(
            (cell % Level.WIDTH) * SIZE + SIZE / 2f,
            (cell / Level.WIDTH) * SIZE + SIZE / 2f
        )
    }

    // Convert world coordinates to cell index
    fun worldToTile(x: Float, y: Float): Int {
        val col = (x / SIZE).toInt()
        val row = (y / SIZE).toInt()
        return row * Level.WIDTH + col
    }
}
```

## Particle System

### Emitter
**Path**: `noosa/Emitter.kt`

Particle emitter base:

```kotlin
class Emitter : Group() {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = 0f
    var height: Float = 0f

    var on: Boolean = false
    var autoKill: Boolean = true

    private var factory: Factory? = null
    private var interval: Float = 0f
    private var quantity: Int = 0

    private var count: Float = 0f

    abstract class Factory {
        abstract fun emit(emitter: Emitter, index: Int, x: Float, y: Float)
    }

    fun start(factory: Factory, interval: Float, quantity: Int) {
        this.factory = factory
        this.interval = interval
        this.quantity = quantity
        on = true
        count = 0f
    }

    fun burst(factory: Factory, quantity: Int) {
        start(factory, 0f, quantity)
    }

    override fun update() {
        if (on) {
            count += Game.elapsed

            if (count >= interval) {
                count -= interval

                val emitX = x + Random.Float(width)
                val emitY = y + Random.Float(height)

                factory?.emit(this, quantity, emitX, emitY)

                if (quantity > 0) {
                    quantity--
                    if (quantity == 0) {
                        on = false
                        if (autoKill) kill()
                    }
                }
            }
        }

        super.update()
    }
}
```

### CellEmitter
**Path**: `effects/CellEmitter.kt`

Cell-based particle emission:

```kotlin
object CellEmitter {
    fun center(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = GameScene.scene!!.emitter()
        emitter.pos(p.x, p.y)
        return emitter
    }

    fun floor(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = GameScene.scene!!.emitter()
        emitter.pos(p.x - 8, p.y + 6, 16f, 4f)
        return emitter
    }

    fun get(cell: Int): Emitter {
        val p = DungeonTilemap.tileToWorld(cell)
        val emitter = GameScene.scene!!.emitter()
        emitter.pos(p.x - 8, p.y - 8, 16f, 16f)
        return emitter
    }
}
```

## Camera System

### Camera
**Path**: `noosa/Camera.kt`

Viewport and transform management:

```kotlin
class Camera(
    var x: Int,
    var y: Int,
    var screenWidth: Int,
    var screenHeight: Int,
    var zoom: Float
) {
    companion object {
        var main: Camera? = null
        val all = ArrayList<Camera>()
    }

    var scroll: PointF = PointF()
    var target: Visual? = null

    private val matrix = FloatArray(16)
    private var shakeTime: Float = 0f
    private var shakeMagnitude: Float = 0f

    init {
        all.add(this)
    }

    fun follow(target: Visual, lerp: Float) {
        this.target = target
        // Smooth scroll to target
        val dx = target.x + target.width / 2 - scroll.x - screenWidth / 2 / zoom
        val dy = target.y + target.height / 2 - scroll.y - screenHeight / 2 / zoom
        scroll.offset(dx * lerp, dy * lerp)
    }

    fun focusOn(visual: Visual) {
        scroll.x = visual.x + visual.width / 2 - screenWidth / 2 / zoom
        scroll.y = visual.y + visual.height / 2 - screenHeight / 2 / zoom
    }

    fun focusOn(cell: Int) {
        focusOn(
            (cell % Level.WIDTH + 0.5f) * DungeonTilemap.SIZE,
            (cell / Level.WIDTH + 0.5f) * DungeonTilemap.SIZE
        )
    }

    fun shake(magnitude: Float, duration: Float) {
        shakeMagnitude = magnitude
        shakeTime = duration
    }

    fun updateMatrix() {
        Matrix.setIdentity(matrix)
        Matrix.translate(matrix, -scroll.x * zoom + x, -scroll.y * zoom + y)
        Matrix.scale(matrix, zoom, zoom)

        if (shakeTime > 0) {
            shakeTime -= Game.elapsed
            val shake = Random.Float(-shakeMagnitude, shakeMagnitude)
            Matrix.translate(matrix, shake, shake)
        }
    }
}
```

## Visual Effects

### Flare
**Path**: `effects/Flare.kt`

Light burst effect:

```kotlin
class Flare(rays: Int, radius: Float) : Visual() {
    private val nRays: Int = rays
    private val radius: Float = radius
    private var duration: Float = 0f
    private var lifespan: Float = 0f

    var color: Int = 0xFFFFFF

    fun show(parent: Group, pos: PointF, duration: Float) {
        this.duration = duration
        this.lifespan = duration

        point(pos)
        parent.add(this)
    }

    override fun update() {
        super.update()

        lifespan -= Game.elapsed
        if (lifespan <= 0) {
            kill()
        } else {
            val progress = lifespan / duration
            scale.set(progress)
            alpha(progress)
        }
    }

    override fun draw() {
        // Draw ray geometry
        for (i in 0 until nRays) {
            val angle = 2 * PI * i / nRays
            // Draw line from center outward
        }
    }
}
```

### Lightning
**Path**: `effects/Lightning.kt`

Lightning bolt effect:

```kotlin
class Lightning(from: Int, to: Int, callback: Callback?) : Group() {
    init {
        val start = DungeonTilemap.tileToWorld(from)
        val end = DungeonTilemap.tileToWorld(to)

        // Generate jagged lightning path
        val arcs = ArrayList<Arc>()
        generateArcs(start, end, arcs)

        arcs.forEach { arc ->
            val segment = LightningSegment(arc)
            add(segment)
        }

        // Play sound
        Sample.INSTANCE.play(Assets.SND_LIGHTNING)

        // Schedule callback
        callback?.let { cb ->
            parent?.add(Delayer(0.1f) { cb.call() })
        }
    }
}
```

## See Also

- [Scene System](scene-system.md) - Scene management
- [UI System](ui-system.md) - UI components
- [Actor System](actor-system.md) - Game entities
