package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.NinePatch
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Blacksmith
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
class WndBlacksmith(troll: Blacksmith, @Suppress("UNUSED_PARAMETER") hero: Hero) : Window() {
    private var btnPressed: ItemButton? = null
    private lateinit var btnItem1: ItemButton
    private lateinit var btnItem2: ItemButton
    private lateinit var btnReforge: RedButton
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                btnPressed?.item(item)
                val item1 = btnItem1.item
                val item2 = btnItem2.item
                if (item1 != null && item2 != null) {
                    val result = Blacksmith.verify(item1, item2)
                    if (result != null) {
                        GameScene.show(WndMessage(result))
                        btnReforge.enable(false)
                    } else {
                        btnReforge.enable(true)
                    }
                }
            }
        }
    }
    init {
        val titlebar = IconTitle()
        troll.sprite()?.let { titlebar.icon(it) }
        titlebar.label(Utils.capitalize(troll.name))
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val message = PixelScene.createMultiline(TXT_PROMPT, 6f)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        btnItem1 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem1
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT)
            }
        }
        btnItem1.setRect(
            (WIDTH - BTN_GAP) / 2 - BTN_SIZE,
            message.y + message.height() + BTN_GAP,
            BTN_SIZE.toFloat(),
            BTN_SIZE.toFloat()
        )
        add(btnItem1)
        btnItem2 = object : ItemButton() {
            override fun onClick() {
                btnPressed = btnItem2
                GameScene.selectItem(itemSelector, WndBag.Mode.UPGRADEABLE, TXT_SELECT)
            }
        }
        btnItem2.setRect(btnItem1.right() + BTN_GAP, btnItem1.top(), BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
        add(btnItem2)
        btnReforge = object : RedButton(TXT_REFORGE) {
            override fun onClick() {
                val i1 = btnItem1.item ?: return
                val i2 = btnItem2.item ?: return
                Blacksmith.upgrade(i1, i2)
                hide()
            }
        }
        btnReforge.enable(false)
        btnReforge.setRect(0f, btnItem1.bottom() + BTN_GAP, WIDTH.toFloat(), 20f)
        add(btnReforge)
        resize(WIDTH, btnReforge.bottom().toInt())
    }
    open class ItemButton : Component() {
        protected lateinit var bg: NinePatch
        protected lateinit var slot: ItemSlot
        var item: Item? = null
        override fun createChildren() {
            super.createChildren()
            bg = Chrome.get(Chrome.Type.BUTTON) ?: return
            add(bg)
            slot = object : ItemSlot() {
                override fun onTouchDown() {
                    bg.brightness(1.2f)
                    Sample.play(Assets.SND_CLICK)
                }
                override fun onTouchUp() {
                    bg.resetColor()
                }
                override fun onClick() {
                    this@ItemButton.onClick()
                }
            }
            add(slot)
        }
        protected open fun onClick() {}
        override fun layout() {
            super.layout()
            bg.x = x
            bg.y = y
            bg.size(width, height)
            slot.setRect(x + 2, y + 2, width - 4, height - 4)
        }
        fun item(item: Item?) {
            this.item = item
            slot.item(item)
        }
    }
    companion object {
        private const val BTN_SIZE = 36
        private const val GAP = 2f
        private const val BTN_GAP = 10f
        private const val WIDTH = 116
        private const val TXT_PROMPT =
            "Ok, a deal is a deal, dat's what I can do for you: I can reforge " +
                    "2 items and turn them into one of a better quality."
        private const val TXT_SELECT = "Select an item to reforge"
        private const val TXT_REFORGE = "Reforge them"
    }
}
