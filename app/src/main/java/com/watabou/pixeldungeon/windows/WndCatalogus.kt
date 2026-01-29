package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
class WndCatalogus : WndTabbed() {
    private val txtTitle: BitmapText
    private val list: ScrollPane
    private val items = ArrayList<ListItem>()
    init {
        if (PixelDungeon.landscape()) {
            resize(WIDTH_L, HEIGHT_L)
        } else {
            resize(WIDTH_P, HEIGHT_P)
        }
        txtTitle = PixelScene.createText(TXT_TITLE, 9f)
        txtTitle.hardlight(Window.TITLE_COLOR)
        txtTitle.measure()
        add(txtTitle)
        list = object : ScrollPane(Component()) {
            override fun onClick(x: Float, y: Float) {
                val size = items.size
                for (i in 0 until size) {
                    if (items[i].onClick(x, y)) {
                        break
                    }
                }
            }
        }
        add(list)
        list.setRect(0f, txtTitle.height(), width.toFloat(), height - txtTitle.height())
        add(object : LabeledTab(TXT_POTIONS) {
            override fun select(value: Boolean) {
                super.select(value)
                showPotions = value
                updateList()
            }
        })
        add(object : LabeledTab(TXT_SCROLLS) {
            override fun select(value: Boolean) {
                super.select(value)
                showPotions = !value
                updateList()
            }
        })
        for (tab in tabs) {
            tab.setSize(TAB_WIDTH.toFloat(), tabHeight().toFloat())
        }
        select(if (showPotions) 0 else 1)
    }
    private fun updateList() {
        txtTitle.text(Utils.format(TXT_TITLE, if (showPotions) TXT_POTIONS else TXT_SCROLLS))
        txtTitle.measure()
        txtTitle.x = PixelScene.align(PixelScene.uiCamera, (width - txtTitle.width()) / 2)
        items.clear()
        val content = list.content
        content.clear()
        list.scrollTo(0f, 0f)
        var pos = 0f
        for (itemClass in if (showPotions) Potion.getKnown() else Scroll.getKnown()) {
            val item = ListItem(itemClass)
            item.setRect(0f, pos, width.toFloat(), ITEM_HEIGHT.toFloat())
            content.add(item)
            items.add(item)
            pos += item.height()
        }
        for (itemClass in if (showPotions) Potion.getUnknown() else Scroll.getUnknown()) {
            val item = ListItem(itemClass)
            item.setRect(0f, pos, width.toFloat(), ITEM_HEIGHT.toFloat())
            content.add(item)
            items.add(item)
            pos += item.height()
        }
        content.setSize(width.toFloat(), pos)
        list.setSize(list.width(), list.height())
    }
    private class ListItem(cl: Class<out Item>) : Component() {
        private var item: Item? = null
        private var identified: Boolean = false
        private lateinit var sprite: ItemSprite
        private lateinit var label: BitmapText
        init {
            try {
                item = cl.getDeclaredConstructor().newInstance()
                identified = item!!.isIdentified
                 // sprite and label are not initialized yet, logic must be in createChildren or later?
                 // But constructor tries to set them.
                 // We should move logic to createChildren or defer.
            } catch (e: Exception) {
                // Do nothing
            }
        }
        override fun createChildren() {
            sprite = ItemSprite()
            add(sprite)
            label = PixelScene.createText(8f)
            add(label)
            // Apply data
            if (item != null) {
                if (identified) {
                    sprite.view(item!!.image(), null)
                    label.text(item!!.name())
                } else {
                    sprite.view(127, null)
                    label.text(item!!.trueName())
                    label.hardlight(0xCCCCCC)
                }
            }
        }
        override fun layout() {
            sprite.y = PixelScene.align(y + (height - sprite.height) / 2)
            label.x = sprite.x + sprite.width
            label.y = PixelScene.align(y + (height - label.baseLine()) / 2)
        }
        fun onClick(x: Float, y: Float): Boolean {
            if (identified && inside(x, y)) {
                GameScene.show(WndInfoItem(item!!))
                return true
            } else {
                return false
            }
        }
    }
    companion object {
        private const val WIDTH_P = 112
        private const val HEIGHT_P = 160
        private const val WIDTH_L = 128
        private const val HEIGHT_L = 128
        private const val ITEM_HEIGHT = 18
        private const val TAB_WIDTH = 50
        private const val TXT_POTIONS = "Potions"
        private const val TXT_SCROLLS = "Scrolls"
        private const val TXT_TITLE = "Catalogus"
        private var showPotions = true
    }
}
