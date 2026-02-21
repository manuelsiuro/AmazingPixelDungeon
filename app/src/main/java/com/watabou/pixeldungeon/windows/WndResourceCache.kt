package com.watabou.pixeldungeon.windows

import com.watabou.gltextures.TextureCache
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import java.util.ArrayList

class WndResourceCache(
    private val cache: NPC,
    private val cacheItems: ArrayList<Item>,
    private val maxItems: Int
) : Window() {

    init {
        val cols = COLS
        val slotStep = SLOT_SIZE + SLOT_MARGIN
        val cacheTotalRows = Math.max(1, (maxItems + cols - 1) / cols)

        // Backpack items
        val hero = Dungeon.hero
        val backpackItems = ArrayList<Item>()
        hero?.belongings?.backpack?.items?.let { backpackItems.addAll(it) }

        val slotsWidth = SLOT_SIZE * cols + SLOT_MARGIN * (cols - 1)

        // Title
        val txtTitle = PixelScene.createText(Utils.capitalize(cache.name), 9f)
        txtTitle.hardlight(TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = (slotsWidth - txtTitle.width()) / 2
        txtTitle.y = (TITLE_HEIGHT - txtTitle.height()) / 2
        add(txtTitle)

        var yPos = TITLE_HEIGHT.toFloat()

        // --- Cache section (scrollable) ---
        val cacheContent = Component()
        val cacheSlots = mutableListOf<CacheSlot>()
        for (i in 0 until maxItems) {
            val col = i % cols
            val row = i / cols
            val item = if (i < cacheItems.size) cacheItems[i] else null
            val slot = CacheSlot(item, true)
            cacheSlots.add(slot)
            cacheContent.add(slot.setPos(
                (col * slotStep).toFloat(), (row * slotStep).toFloat()
            ))
        }
        val cacheContentHeight = (cacheTotalRows * slotStep).toFloat()
        cacheContent.setSize(slotsWidth.toFloat(), cacheContentHeight)
        val cacheVisibleHeight = minOf(cacheContentHeight, (VISIBLE_ROWS * slotStep).toFloat())

        val cacheList = object : ScrollPane(cacheContent) {
            override fun onClick(x: Float, y: Float) {
                for (slot in cacheSlots) {
                    if (slot.handleClick(x, y)) break
                }
            }
        }
        add(cacheList)
        val cacheListY = yPos
        yPos += cacheVisibleHeight + GAP

        // Divider
        val divider = ColorBlock(slotsWidth.toFloat(), 1f, 0xFF222222.toInt())
        divider.x = 0f
        divider.y = yPos
        add(divider)
        yPos += 1f + GAP

        // Backpack label
        val txtBackpack = PixelScene.createText("Backpack", 7f)
        txtBackpack.hardlight(0xCCCCCC)
        txtBackpack.measure()
        txtBackpack.x = (slotsWidth - txtBackpack.width()) / 2
        txtBackpack.y = yPos
        add(txtBackpack)
        yPos += LABEL_HEIGHT

        // --- Backpack section (scrollable) ---
        val bpContent = Component()
        val bpSlots = mutableListOf<CacheSlot>()
        for (i in backpackItems.indices) {
            val col = i % cols
            val row = i / cols
            val slot = CacheSlot(backpackItems[i], false)
            bpSlots.add(slot)
            bpContent.add(slot.setPos(
                (col * slotStep).toFloat(), (row * slotStep).toFloat()
            ))
        }
        // Fill remaining slots in last row
        val remaining = if (backpackItems.size % cols == 0 && backpackItems.isNotEmpty()) 0
                        else cols - (backpackItems.size % cols)
        for (i in 0 until remaining) {
            if (backpackItems.isEmpty() && i == 0) continue
            val idx = backpackItems.size + i
            val col = idx % cols
            val row = idx / cols
            val slot = CacheSlot(null, false)
            bpSlots.add(slot)
            bpContent.add(slot.setPos(
                (col * slotStep).toFloat(), (row * slotStep).toFloat()
            ))
        }
        val totalBpSlots = if (backpackItems.isEmpty()) 1 else backpackItems.size + remaining
        val bpTotalRows = (totalBpSlots + cols - 1) / cols
        val bpContentHeight = (bpTotalRows * slotStep).toFloat()
        bpContent.setSize(slotsWidth.toFloat(), bpContentHeight)
        val bpVisibleHeight = minOf(bpContentHeight, (VISIBLE_ROWS * slotStep).toFloat())

        val bpList = object : ScrollPane(bpContent) {
            override fun onClick(x: Float, y: Float) {
                for (slot in bpSlots) {
                    if (slot.handleClick(x, y)) break
                }
            }
        }
        add(bpList)
        val bpListY = yPos
        yPos += bpVisibleHeight

        // Critical setup order: add (done above) -> resize -> setRect
        resize(slotsWidth, yPos.toInt())
        cacheList.setRect(0f, cacheListY, slotsWidth.toFloat(), cacheVisibleHeight)
        bpList.setRect(0f, bpListY, slotsWidth.toFloat(), bpVisibleHeight)
    }

    private fun transferToCache(item: Item) {
        if (cacheItems.size >= maxItems) {
            GLog.w("The resource cache is full!")
            return
        }
        val hero = Dungeon.hero ?: return

        // Handle stackable items already in cache
        if (item.stackable) {
            for (existing in cacheItems) {
                if (existing.javaClass == item.javaClass) {
                    existing.quantity += item.quantity()
                    item.detachAll(hero.belongings.backpack)
                    Sample.play(Assets.SND_ITEM)
                    refreshWindow()
                    return
                }
            }
        }

        val detached = item.detachAll(hero.belongings.backpack)
        cacheItems.add(detached)
        Sample.play(Assets.SND_ITEM)
        refreshWindow()
    }

    private fun transferToBackpack(item: Item) {
        val hero = Dungeon.hero ?: return

        if (!item.collect(hero.belongings.backpack)) {
            GLog.w("Your backpack is full!")
            return
        }

        cacheItems.remove(item)
        Sample.play(Assets.SND_ITEM)
        refreshWindow()
    }

    private fun refreshWindow() {
        hide()
        com.watabou.pixeldungeon.scenes.GameScene.show(
            WndResourceCache(cache, cacheItems, maxItems)
        )
    }

    private inner class CacheSlot(
        private val slotItem: Item?,
        private val isCacheSlot: Boolean
    ) : ItemSlot() {

        private lateinit var bg: ColorBlock

        init {
            width = SLOT_SIZE.toFloat()
            height = SLOT_SIZE.toFloat()
            item(slotItem)
        }

        override fun createChildren() {
            bg = ColorBlock(SLOT_SIZE.toFloat(), SLOT_SIZE.toFloat(), NORMAL)
            add(bg)
            super.createChildren()
        }

        override fun layout() {
            bg.x = x
            bg.y = y
            super.layout()
        }

        override fun item(item: Item?) {
            super.item(item)
            if (item != null) {
                bg.texture(TextureCache.createSolid(
                    if (isCacheSlot) CACHE_COLOR else NORMAL
                ))
            } else {
                bg.color(if (isCacheSlot) CACHE_EMPTY else NORMAL)
            }
        }

        override fun onTouchDown() {
            bg.brightness(1.5f)
            Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        override fun onTouchUp() {
            bg.brightness(1.0f)
        }

        fun handleClick(x: Float, y: Float): Boolean {
            if (inside(x, y)) {
                onClick()
                return true
            }
            return false
        }

        override fun onClick() {
            val item = slotItem ?: return
            if (isCacheSlot) {
                transferToBackpack(item)
            } else {
                transferToCache(item)
            }
        }

        override fun onLongClick(): Boolean {
            val item = slotItem ?: return false
            this@WndResourceCache.add(WndItem(null, item))
            return true
        }
    }

    companion object {
        private const val COLS = 4
        private const val SLOT_SIZE = 28
        private const val SLOT_MARGIN = 1
        private const val TITLE_HEIGHT = 12
        private const val LABEL_HEIGHT = 10
        private const val GAP = 4
        private const val VISIBLE_ROWS = 2

        private const val NORMAL = 0xFF4A4D44.toInt()
        private const val CACHE_COLOR = 0xFF4A5A33.toInt()
        private const val CACHE_EMPTY = 0xFF3A4228.toInt()
    }
}
