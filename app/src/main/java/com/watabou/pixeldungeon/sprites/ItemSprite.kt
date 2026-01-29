package com.watabou.pixeldungeon.sprites
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.Game
import com.watabou.noosa.MovieClip
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.PointF
import com.watabou.utils.Random
open class ItemSprite : MovieClip {
    var heap: Heap? = null
    private var glowing: Glowing? = null
    private var phase: Float = 0f
    private var glowUp: Boolean = false
    private var dropInterval: Float = 0f
    constructor() : this(ItemSpriteSheet.SMTH, null)
    constructor(item: Item) : this(item.image(), item.glowing())
    constructor(image: Int, glowing: Glowing?) : super(Assets.ITEMS) {
        if (film == null) {
            film = TextureFilm(checkNotNull(texture) { "Texture must be set" }, SIZE, SIZE)
        }
        view(image, glowing)
    }
    fun originToCenter() {
        origin.set((SIZE / 2).toFloat())
    }
    fun link() {
        heap?.let { link(it) }
    }
    fun link(heap: Heap) {
        this.heap = heap
        view(heap.image(), heap.glowing())
        place(heap.pos)
    }
    override fun revive() {
        super.revive()
        speed.set(0f)
        acc.set(0f)
        dropInterval = 0f
        heap = null
    }
    fun worldToCamera(cell: Int): PointF {
        val csize = DungeonTilemap.SIZE
        return PointF(
            (cell % Level.WIDTH) * csize + (csize - SIZE) * 0.5f,
            (cell / Level.WIDTH) * csize + (csize - SIZE) * 0.5f
        )
    }
    fun place(p: Int) {
        point(worldToCamera(p))
    }
    open fun drop() {
        val currentHeap = heap ?: return
        if (currentHeap.isEmpty) {
            return
        }
        dropInterval = DROP_INTERVAL
        speed.set(0f, -100f)
        acc.set(0f, -speed.y / DROP_INTERVAL * 2)
        if (visible && currentHeap.peek() is Gold) {
            CellEmitter.center(currentHeap.pos).burst(Speck.factory(Speck.COIN), 5)
            Sample.play(Assets.SND_GOLD, 1f, 1f, Random.Float(0.9f, 1.1f))
        }
    }
    fun drop(from: Int) {
        val currentHeap = heap ?: return
        if (currentHeap.pos == from) {
            drop()
        } else {
            val px = x
            val py = y
            drop()
            place(from)
            speed.offset((px - x) / DROP_INTERVAL, (py - y) / DROP_INTERVAL)
        }
    }
    fun view(image: Int, glowing: Glowing?): ItemSprite {
        film?.get(image)?.let { frame(it) }
        this.glowing = glowing
        if (glowing == null) {
            resetColor()
        }
        return this
    }
    override fun update() {
        super.update()
        val currentHeap = heap
        visible = currentHeap == null || Dungeon.visible[currentHeap.pos]
        if (dropInterval > 0) {
            dropInterval -= Game.elapsed
            if (dropInterval <= 0) {
                speed.set(0f)
                acc.set(0f)
                currentHeap?.let { h ->
                    place(h.pos)
                    if (visible) {
                        var water = Level.water[h.pos]
                        if (water) {
                            GameScene.ripple(h.pos)
                        } else {
                            val level = Dungeon.level ?: return
                            val cell = level.map[h.pos]
                            water = cell == Terrain.WELL || cell == Terrain.ALCHEMY
                        }
                        if (h.peek() !is Gold) {
                            Sample.play(
                                if (water) Assets.SND_WATER else Assets.SND_STEP,
                                0.8f, 0.8f, 1.2f
                            )
                        }
                    }
                }
            }
        }
        val currentGlowing = glowing
        if (visible && currentGlowing != null) {
            if (glowUp) {
                phase += Game.elapsed
                if (phase > currentGlowing.period) {
                    glowUp = false
                    phase = currentGlowing.period
                }
            } else {
                phase -= Game.elapsed
                if (phase < 0) {
                    glowUp = true
                    phase = 0f
                }
            }
            val value = phase / currentGlowing.period * 0.6f
            rm = 1 - value
            gm = rm
            bm = rm
            ra = currentGlowing.red * value
            ga = currentGlowing.green * value
            ba = currentGlowing.blue * value
        }
    }
    class Glowing(
        val color: Int,
        val period: Float = 1f
    ) {
        val red: Float = (color shr 16) / 255f
        val green: Float = ((color shr 8) and 0xFF) / 255f
        val blue: Float = (color and 0xFF) / 255f
        companion object {
            val WHITE: Glowing = Glowing(0xFFFFFF, 0.6f)
        }
    }
    companion object {
        const val SIZE: Int = 16
        private const val DROP_INTERVAL: Float = 0.4f
        protected var film: TextureFilm? = null
        fun pick(index: Int, x: Int, y: Int): Int {
            val bmp = TextureCache.get(Assets.ITEMS).bitmap ?: return 0
            val rows = bmp.width / SIZE
            val row = index / rows
            val col = index % rows
            return bmp.getPixel(col * SIZE + x, row * SIZE + y)
        }
    }
}
