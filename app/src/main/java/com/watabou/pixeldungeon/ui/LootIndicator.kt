package com.watabou.pixeldungeon.ui
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
class LootIndicator : Tag(0x1F75CC) {
    private lateinit var slot: ItemSlot
    private var lastItem: Item? = null
    private var lastQuantity = 0
    init {
        setSize(24f, 22f)
        visible = false
    }
    override fun createChildren() {
        super.createChildren()
        // Anonymous subclass of ItemSlot (which is still Java, assuming it's not final)
        slot = object : ItemSlot() {
            override fun onClick() {
                val hero = Dungeon.hero ?: return
                hero.handle(hero.pos)
            }
        }
        slot.showParams(false)
        add(slot)
    }
    override fun layout() {
        super.layout()
        slot.setRect(x + 2, y + 3, width - 2, height - 6)
    }
    override fun update() {
        val hero = Dungeon.hero
        val level = Dungeon.level
        if (hero != null && hero.ready && level != null) {
            val heroPos = hero.pos
            // heaps is SparseArray. In Kotlin/Java integration, get might be needed or brackets.
            // Using get() explicitly.
            val heap = level.heaps.get(heroPos)
            if (heap != null && heap.type !== Heap.Type.HIDDEN) {
                val item = when (heap.type) {
                    Heap.Type.CHEST, Heap.Type.MIMIC -> ItemSlot.CHEST
                    Heap.Type.LOCKED_CHEST -> ItemSlot.LOCKED_CHEST
                    Heap.Type.TOMB -> ItemSlot.TOMB
                    Heap.Type.SKELETON -> ItemSlot.SKELETON
                    else -> heap.peek()
                }
                if (item != null && (item !== lastItem || item.quantity() != lastQuantity)) {
                    lastItem = item
                    lastQuantity = item.quantity()
                    slot.item(item)
                    flash()
                }
                visible = true
            } else {
                lastItem = null
                visible = false
            }
        }
        slot.enable(visible && (Dungeon.hero?.ready == true))
        super.update()
    }
}
