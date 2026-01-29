package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Button
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.CellSelector
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.pixeldungeon.windows.WndCatalogus
import com.watabou.pixeldungeon.windows.WndHero
import com.watabou.pixeldungeon.windows.WndInfoCell
import com.watabou.pixeldungeon.windows.WndInfoItem
import com.watabou.pixeldungeon.windows.WndInfoMob
import com.watabou.pixeldungeon.windows.WndInfoPlant
import com.watabou.pixeldungeon.windows.WndMessage
import com.watabou.pixeldungeon.windows.WndTradeItem
import kotlin.math.sqrt
class Toolbar : Component() {
    private lateinit var btnWait: Tool
    private lateinit var btnSearch: Tool
    private lateinit var btnInfo: Tool
    private lateinit var btnInventory: Tool
    private lateinit var btnQuick1: Tool
    private lateinit var btnQuick2: Tool
    private lateinit var pickedUp: PickedUpItem
    private var lastEnabled = true
    init {
        instance = this
        // Constructor finishes, height set?
        // createChildren sets btnInventory?
    }
    // In Java constructor set height = btnInventory.height().
    // createChildren is called from super().
    // So buttons are created.
    // BUT in Kotlin, property initializers run AFTER super().
    // If createChildren accesses properties, they must be initialized or handle null or use lazy/lateinit if guaranteed.
    // 'btnWait' etc are init in createChildren.
    override fun createChildren() {
        btnWait = object : Tool(0, 7, 20, 25) {
            override fun onClick() {
                Dungeon.hero?.rest(false)
            }
            override fun onLongClick(): Boolean {
                Dungeon.hero?.rest(true)
                return true
            }
        }
        add(btnWait)
        btnSearch = object : Tool(20, 7, 20, 25) {
            override fun onClick() {
                Dungeon.hero?.search(true)
            }
        }
        add(btnSearch)
        btnInfo = object : Tool(40, 7, 21, 25) {
            override fun onClick() {
                GameScene.selectCell(informer)
            }
        }
        add(btnInfo)
        btnInventory = object : Tool(60, 7, 23, 25) {
            private lateinit var gold: GoldIndicator
            override fun onClick() {
                val hero = Dungeon.hero ?: return
                GameScene.show(WndBag(hero.belongings.backpack, null, WndBag.Mode.ALL, null))
            }
            override fun onLongClick(): Boolean {
                GameScene.show(WndCatalogus())
                return true
            }
            override fun createChildren() {
                super.createChildren()
                gold = GoldIndicator()
                add(gold)
            }
            override fun layout() {
                super.layout()
                gold.fill(this)
            }
        }
        add(btnInventory)
        btnQuick1 = QuickslotTool(83, 7, 22, 25, true)
        add(btnQuick1)
        btnQuick2 = QuickslotTool(83, 7, 22, 25, false)
        add(btnQuick2)
        btnQuick2.visible = (QuickSlot.secondaryValue != null)
        pickedUp = PickedUpItem()
        add(pickedUp)
        height = btnInventory.height()
    }
    override fun layout() {
        btnWait.setPos(x, y)
        btnSearch.setPos(btnWait.right(), y)
        btnInfo.setPos(btnSearch.right(), y)
        btnQuick1.setPos(width - btnQuick1.width(), y)
        if (btnQuick2.visible) {
            btnQuick2.setPos(btnQuick1.left() - btnQuick2.width(), y)
            btnInventory.setPos(btnQuick2.left() - btnInventory.width(), y)
        } else {
            btnInventory.setPos(btnQuick1.left() - btnInventory.width(), y)
        }
    }
    override fun update() {
        super.update()
        val hero = Dungeon.hero ?: return
        if (lastEnabled != hero.ready) {
            lastEnabled = hero.ready
            for (i in 0 until length) { // Access members via length/get?
                val tool = members[i]
                if (tool is Tool) {
                    tool.enable(lastEnabled)
                }
            }
        }
        if (!hero.isAlive) {
            btnInventory.enable(true)
        }
    }
    fun pickup(item: Item) {
        pickedUp.reset(
            item,
            btnInventory.centerX(),
            btnInventory.centerY()
        )
    }
    private open class Tool(x: Int, y: Int, width: Int, height: Int) : Button() {
        protected lateinit var base: Image
        init {
            // base initialized in createChildren which is called by super()
            // But we set base.frame here.
            // If base is lateinit, it must be init in createChildren.
            // Wait, super() calls createChildren().
            // createChildren() uses 'base = Image(...)'.
            // THEN we are back in init block?
            // Yes.
            base.frame(x, y, width, height)
            this.width = width.toFloat()
            this.height = height.toFloat()
        }
        override fun createChildren() {
            super.createChildren()
            base = Image(Assets.TOOLBAR)
            add(base)
        }
        override fun layout() {
            super.layout()
            base.x = x
            base.y = y
        }
        override fun onTouchDown() {
            base.brightness(1.4f)
        }
        override fun onTouchUp() {
            if (active) {
                base.resetColor()
            } else {
                base.tint(BGCOLOR, 0.7f)
            }
        }
        open fun enable(value: Boolean) {
            if (value != active) {
                if (value) {
                    base.resetColor()
                } else {
                    base.tint(BGCOLOR, 0.7f)
                }
                active = value
            }
        }
        companion object {
            const val BGCOLOR = 0x7B8073.toInt()
        }
    }
    private class QuickslotTool(x: Int, y: Int, width: Int, height: Int, primary: Boolean) :
        Tool(x, y, width, height) {
        private lateinit var slot: QuickSlot
        init {
            if (primary) {
                slot.primary()
            } else {
                slot.secondary()
            }
        }
        override fun createChildren() {
            super.createChildren()
            slot = QuickSlot()
            add(slot)
        }
        override fun layout() {
            super.layout()
            slot.setRect(x + 1, y + 2, width - 2, height - 2)
        }
        override fun enable(value: Boolean) {
            slot.enable(value)
            super.enable(value)
        }
    }
    private class PickedUpItem : ItemSprite() {
        private var dstX: Float = 0f
        private var dstY: Float = 0f
        private var left: Float = 0f
        init {
            originToCenter()
            visible = false
            active = false
        }
        fun reset(item: Item, dstX: Float, dstY: Float) {
            view(item.image(), item.glowing())
            visible = true
            active = true
            this.dstX = dstX - ItemSprite.SIZE / 2
            this.dstY = dstY - ItemSprite.SIZE / 2
            left = DURATION
            x = this.dstX - DISTANCE
            y = this.dstY - DISTANCE
            alpha(1f)
        }
        override fun update() {
            super.update()
            left -= Game.elapsed
            if (left <= 0) {
                active = false
                visible = false
            } else {
                val p = left / DURATION
                scale.set(sqrt(p.toDouble()).toFloat())
                val offset = DISTANCE * p
                x = dstX - offset
                y = dstY - offset
            }
        }
        companion object {
            const val DISTANCE = DungeonTilemap.SIZE.toFloat()
            const val DURATION = 0.2f
        }
    }
    companion object {
        private lateinit var instance: Toolbar
        fun secondQuickslot(): Boolean {
            return instance.btnQuick2.visible
        }
        fun secondQuickslot(value: Boolean) {
            instance.btnQuick2.visible = value
            instance.btnQuick2.active = value
            instance.layout()
        }
        private val informer = object : CellSelector.Listener {
            override fun onSelect(cell: Int?) {
                if (cell == null) {
                    return
                }
                val level = Dungeon.level ?: return
                if (cell < 0 || cell >= Level.LENGTH || (!level.visited[cell] && !level.mapped[cell])) {
                    GameScene.show(WndMessage("You don't know what is there."))
                    return
                }
                val visible = Dungeon.visible
                if (!visible[cell]) {
                    GameScene.show(WndInfoCell(cell))
                    return
                }
                val hero = Dungeon.hero ?: return
                if (cell == hero.pos) {
                    GameScene.show(WndHero())
                    return
                }
                val mob = Actor.findChar(cell) as? Mob
                if (mob != null) {
                    GameScene.show(WndInfoMob(mob))
                    return
                }
                val heap = level.heaps[cell]
                if (heap != null && heap.type != Heap.Type.HIDDEN) {
                    val peekItem = heap.peek()
                    if (heap.type == Heap.Type.FOR_SALE && heap.size() == 1 && peekItem != null && peekItem.price() > 0) {
                        GameScene.show(WndTradeItem(heap, false))
                    } else {
                        GameScene.show(WndInfoItem(heap))
                    }
                    return
                }
                val plant = level.plants[cell]
                if (plant != null) {
                    GameScene.show(WndInfoPlant(plant))
                    return
                }
                GameScene.show(WndInfoCell(cell))
            }
            override fun prompt(): String {
                return "Select a cell to examine"
            }
        }
    }
}
