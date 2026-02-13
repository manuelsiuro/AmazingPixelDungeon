package com.watabou.pixeldungeon.scenes

import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.encyclopedia.EncyclopediaCategory
import com.watabou.pixeldungeon.encyclopedia.EncyclopediaEntry
import com.watabou.pixeldungeon.encyclopedia.EncyclopediaRegistry
import com.watabou.pixeldungeon.encyclopedia.IconType
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.windows.WndEncyclopediaEntry

class EncyclopediaScene : PixelScene() {

    private var selectedCategory: EncyclopediaCategory = EncyclopediaCategory.WEAPONS
    private val categoryButtons = mutableListOf<RedButton>()
    private lateinit var scrollPane: ScrollPane
    private val entryItems = mutableListOf<EntryItem>()

    override fun create() {
        super.create()

        Music.play(Assets.THEME, true)
        Music.volume(1f)

        uiCamera.visible = false

        val w = Camera.main!!.width
        val h = Camera.main!!.height

        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)

        val title = createText(TXT_TITLE, 9f)
        title.hardlight(Window.TITLE_COLOR)
        title.measure()
        title.x = align((w - title.width()) / 2)
        title.y = align(TITLE_TOP)
        add(title)

        val btnExit = ExitButton()
        btnExit.setPos(Camera.main!!.width - btnExit.width(), 0f)
        add(btnExit)

        // Category buttons
        val categories = EncyclopediaCategory.values()
        val numCols = if (PixelDungeon.landscape()) 6 else 4
        val numRows = (categories.size + numCols - 1) / numCols
        val btnGap = 1f
        val catAreaTop = title.y + title.height() + CAT_GAP
        val catAreaWidth = w - MARGIN * 2
        val btnWidth = (catAreaWidth - (numCols - 1) * btnGap) / numCols
        val btnHeight = BTN_HEIGHT

        for (i in categories.indices) {
            val cat = categories[i]
            val col = i % numCols
            val row = i / numCols

            val btn = object : RedButton(cat.displayName) {
                override fun onClick() {
                    Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
                    selectCategory(cat)
                }
            }
            btn.setRect(
                MARGIN + col * (btnWidth + btnGap),
                catAreaTop + row * (btnHeight + btnGap),
                btnWidth,
                btnHeight
            )
            add(btn)
            categoryButtons.add(btn)
        }

        val catAreaBottom = catAreaTop + numRows * (btnHeight + btnGap)

        // ScrollPane for entries
        scrollPane = object : ScrollPane(Component()) {
            override fun onClick(x: Float, y: Float) {
                for (item in entryItems) {
                    if (item.onClick(x, y)) break
                }
            }
        }
        add(scrollPane)
        scrollPane.setRect(MARGIN, catAreaBottom + CAT_GAP, (w - MARGIN * 2).toFloat(), (h - catAreaBottom - CAT_GAP - MARGIN).toFloat())

        selectCategory(EncyclopediaCategory.WEAPONS)

        fadeIn()
    }

    private fun selectCategory(cat: EncyclopediaCategory) {
        selectedCategory = cat

        // Update button colors
        val categories = EncyclopediaCategory.values()
        for (i in categories.indices) {
            if (i < categoryButtons.size) {
                if (categories[i] == cat) {
                    categoryButtons[i].textColor(Window.TITLE_COLOR)
                } else {
                    categoryButtons[i].textColor(0xCCCCCC)
                }
            }
        }

        refreshList()
    }

    private fun refreshList() {
        entryItems.clear()
        val content = scrollPane.content
        content.clear()
        scrollPane.scrollTo(0f, 0f)

        val allEntries = EncyclopediaRegistry.forCategory(selectedCategory)
        val subcategories = allEntries.map { it.subcategory }.distinct()

        val listWidth = scrollPane.width()
        var pos = 0f

        for (subcat in subcategories) {
            // Subcategory header
            if (subcat.isNotEmpty()) {
                val header = createText(subcat, 7f)
                header.hardlight(Window.TITLE_COLOR)
                header.measure()
                header.x = 2f
                header.y = pos + 2f
                content.add(header)
                pos += header.height() + 4f
            }

            // Entries in this subcategory
            val entries = allEntries.filter { it.subcategory == subcat }
            for (entry in entries) {
                val item = EntryItem()
                item.setData(entry)
                item.setRect(0f, pos, listWidth, ITEM_HEIGHT.toFloat())
                content.add(item)
                entryItems.add(item)
                pos += ITEM_HEIGHT
            }
        }

        content.setSize(listWidth, pos)
        scrollPane.setSize(scrollPane.width(), scrollPane.height())
    }

    override fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }

    private class EntryItem : Component() {
        private lateinit var icon: Image
        private lateinit var label: BitmapText
        private lateinit var separator: ColorBlock
        var entry: EncyclopediaEntry? = null

        override fun createChildren() {
            icon = ItemSprite()
            add(icon)
            label = createText(7f)
            add(label)
            separator = ColorBlock(1f, 1f, 0x22FFFFFF.toInt())
            add(separator)
        }

        fun setData(data: EncyclopediaEntry) {
            this.entry = data

            // Replace icon based on type
            remove(icon)
            icon = when (data.iconType) {
                IconType.ITEM, IconType.CUSTOM -> ItemSprite(data.iconImage, null)
                IconType.MOB -> {
                    val img = Image(data.spriteTexture)
                    img.frame(img.texture!!.uvRect(0, 0, data.spriteWidth, data.spriteHeight))
                    img
                }
                IconType.BUFF -> {
                    if (data.iconImage >= 0) {
                        val img = Image(Assets.BUFFS_SMALL)
                        val film = TextureFilm(img.texture!!, BuffIndicator.SIZE, BuffIndicator.SIZE)
                        film.get(data.iconImage)?.let { img.frame(it) }
                        img
                    } else {
                        ItemSprite(ItemSpriteSheet.TORCH, null)
                    }
                }
            }
            add(icon)

            label.text(data.name)
            label.measure()
        }

        override fun layout() {
            icon.x = x + 1
            icon.y = align(y + (height - icon.height) / 2)
            label.x = icon.x + icon.width + 2
            label.y = align(y + (height - label.baseLine()) / 2)
            separator.size(width, 1f)
            separator.x = x
            separator.y = y + height - 1
        }

        fun onClick(touchX: Float, touchY: Float): Boolean {
            val e = entry ?: return false
            if (touchX >= x && touchX < x + width && touchY >= y && touchY < y + height) {
                Game.scene()!!.add(WndEncyclopediaEntry(e))
                return true
            }
            return false
        }
    }

    companion object {
        private const val TXT_TITLE = "Guide"
        private const val TITLE_TOP = 2f
        private const val MARGIN = 2f
        private const val CAT_GAP = 4f
        private const val BTN_HEIGHT = 14f
        private const val ITEM_HEIGHT = 18
    }
}
