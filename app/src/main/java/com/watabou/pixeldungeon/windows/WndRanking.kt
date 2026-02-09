package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Game
import com.watabou.noosa.Gizmo
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.ui.BadgesList
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.utils.Utils
import java.util.Locale
class WndRanking(gameFile: String) : WndTabbed() {
    private var thread: Thread?
    private var error: String? = null
    private var busy: Image? = null
    init {
        resize(WIDTH, HEIGHT)
        thread = object : Thread() {
            override fun run() {
                try {
                    Badges.loadGlobal()
                    Dungeon.loadGame(gameFile)
                } catch (e: Exception) {
                    error = TXT_ERROR
                }
            }
        }
        thread!!.start()
        busy = Icons.BUSY.get()
        busy!!.origin.set(busy!!.width / 2f, busy!!.height / 2f)
        busy!!.angularSpeed = 720f
        busy!!.x = (WIDTH - busy!!.width) / 2f
        busy!!.y = (HEIGHT - busy!!.height) / 2f
        add(busy!!)
    }
    override fun update() {
        super.update()
        if (thread != null && !thread!!.isAlive) {
            thread = null
            if (error == null) {
                remove(busy!!)
                createControls()
            } else {
                hide()
                Game.scene()!!.add(WndError(TXT_ERROR))
            }
        }
    }
    private fun createControls() {
        val labels = arrayOf(TXT_STATS, TXT_ITEMS, TXT_BADGES)
        val pages = arrayOf<Group>(StatsTab(), ItemsTab(), BadgesTab())
        for (i in pages.indices) {
            add(pages[i])
            val tab = RankingTab(labels[i], pages[i])
            tab.setSize(TAB_WIDTH.toFloat(), tabHeight().toFloat())
            add(tab)
        }
        select(0)
    }
    private inner class RankingTab(label: String, private val page: Group) : LabeledTab(label) {
        override fun select(value: Boolean) {
            super.select(value)
            page.visible = selected
            page.active = selected
        }
    }
    private inner class StatsTab : Group() {
        init {
            val heroClass = Dungeon.hero!!.className()
            val title = IconTitle()
            title.icon(HeroSprite.avatar(Dungeon.hero!!.heroClass, Dungeon.hero!!.tier()))
            title.label(Utils.format(TXT_TITLE, Dungeon.hero!!.lvl, heroClass).uppercase(Locale.ENGLISH))
            title.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(title)
            var pos = title.bottom()
            if (Dungeon.challenges > 0) {
                val btnCatalogus = object : RedButton(TXT_CHALLENGES) {
                    override fun onClick() {
                        Game.scene()!!.add(WndChallenges(Dungeon.challenges, false))
                    }
                }
                btnCatalogus.setRect(
                    0f,
                    pos + GAP,
                    btnCatalogus.reqWidth() + 2,
                    btnCatalogus.reqHeight() + 2
                )
                add(btnCatalogus)
                pos = btnCatalogus.bottom()
            }
            pos += (GAP + GAP).toFloat()
            pos = statSlot(this, TXT_STR, Integer.toString(Dungeon.hero!!.STR), pos)
            pos = statSlot(this, TXT_HEALTH, Integer.toString(Dungeon.hero!!.HT), pos)
            pos += GAP.toFloat()
            pos = statSlot(this, TXT_DURATION, Integer.toString(Statistics.duration.toInt()), pos)
            pos += GAP.toFloat()
            pos = statSlot(this, TXT_DEPTH, Integer.toString(Statistics.deepestFloor), pos)
            pos = statSlot(this, TXT_ENEMIES, Integer.toString(Statistics.enemiesSlain), pos)
            pos = statSlot(this, TXT_GOLD, Integer.toString(Statistics.goldCollected), pos)
            pos += GAP.toFloat()
            pos = statSlot(this, TXT_FOOD, Integer.toString(Statistics.foodEaten), pos)
            pos = statSlot(this, TXT_ALCHEMY, Integer.toString(Statistics.potionsCooked), pos)
            statSlot(this, TXT_ANKHS, Integer.toString(Statistics.ankhsUsed), pos)
        }
        private fun statSlot(parent: Group, label: String, value: String, pos: Float): Float {
            var txt = PixelScene.createText(label, 7f)
            txt.y = pos
            parent.add(txt)
            txt = PixelScene.createText(value, 7f)
            txt.measure()
            txt.x = PixelScene.align(WIDTH * 0.65f)
            txt.y = pos
            parent.add(txt)
            return (pos + GAP + txt.baseLine())
        }
    }
    private inner class ItemsTab : Group() {
        private var count = 0
        private var pos = 0f
        init {
            val stuff = Dungeon.hero!!.belongings
            if (stuff.weapon != null) {
                addItem(stuff.weapon)
            }
            if (stuff.armor != null) {
                addItem(stuff.armor)
            }
            if (stuff.ring1 != null) {
                addItem(stuff.ring1)
            }
            if (stuff.ring2 != null) {
                addItem(stuff.ring2)
            }
            val primary = getQuickslot(QuickSlot.primaryValue)
            val secondary = getQuickslot(QuickSlot.secondaryValue)
            if (count >= 4 && primary != null && secondary != null) {
                val size = ITEM_BUTTON_SIZE
                var slot = ItemButton(primary)
                slot.setRect(0f, pos, size.toFloat(), size.toFloat())
                add(slot)
                slot = ItemButton(secondary)
                slot.setRect((size + 1).toFloat(), pos, size.toFloat(), size.toFloat())
                add(slot)
            } else {
                if (primary != null) {
                    addItem(primary)
                }
                if (secondary != null) {
                    addItem(secondary)
                }
            }
        }
        private fun addItem(item: Item?) {
            if (item == null) return
            val slot = LabelledItemButton(item)
            val btnHeight = LABELLED_ITEM_BUTTON_SIZE
            slot.setRect(0f, pos, width.toFloat(), btnHeight)
            add(slot)
            pos += slot.height() + 1
            count++
        }
        private fun getQuickslot(value: Any?): Item? {
            if (value is Item && Dungeon.hero!!.belongings.backpack.contains(value)) {
                return value
            } else if (value is Class<*>) {
                @Suppress("UNCHECKED_CAST")
                val itemClass = value as Class<out Item>
                val item = Dungeon.hero!!.belongings.getItem(itemClass)
                if (item != null) {
                    return item
                }
            }
            return null
        }
    }
    private inner class BadgesTab : Group() {
        init {
            camera = this@WndRanking.camera
            val list = BadgesList(false)
            add(list)
            list.setSize(WIDTH.toFloat(), HEIGHT.toFloat())
        }
    }
    private open inner class ItemButton(protected var item: Item) : Button() {
        protected lateinit var slot: ItemSlot
        private lateinit var bg: ColorBlock
        override fun createChildren() {
            bg = ColorBlock(ITEM_BUTTON_SIZE.toFloat(), ITEM_BUTTON_SIZE.toFloat(), 0xFF4A4D44.toInt())
            add(bg)
            slot = ItemSlot()
            add(slot)
            super.createChildren()
        }
        // init runs after property initializers (item is ready)
        init {
            slot.item(item)
            if (item.cursed && item.cursedKnown) {
                bg.ra = +0.2f
                bg.ga = -0.1f
            } else if (!item.isIdentified) {
                bg.ra = 0.1f
                bg.ba = 0.1f
            }
        }
        override fun layout() {
            bg.x = x
            bg.y = y
            slot.setRect(x, y, ITEM_BUTTON_SIZE.toFloat(), ITEM_BUTTON_SIZE.toFloat())
            super.layout()
        }
        override fun onTouchDown() {
            bg.brightness(1.5f)
            Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }
         override fun onTouchUp() {
            bg.brightness(1.0f)
        }
        override fun onClick() {
            Game.scene()!!.add(WndItem(null, item))
        }
    }
    private inner class LabelledItemButton(item: Item) : ItemButton(item) {
        private var name: BitmapText? = null
        override fun createChildren() {
            super.createChildren()
            name = PixelScene.createText("?", 7f)
            add(name!!)
        }
        override fun layout() {
            super.layout()
            val n = name!!
            n.x = slot.right() + 2
            n.y = y + (height - n.baseLine()) / 2
            var str = Utils.capitalize(item.name())
            n.text(str)
            n.measure()
            if (n.width() > width - n.x) {
                do {
                    str = str.substring(0, str.length - 1)
                    n.text("$str...")
                    n.measure()
                } while (n.width() > width - n.x)
            }
        }
    }
    companion object {
        private const val TXT_ERROR = "Unable to load additional information"
        private const val TXT_STATS = "Stats"
        private const val TXT_ITEMS = "Items"
        private const val TXT_BADGES = "Badges"
        private const val TXT_TITLE = "Level %d %s"
        private const val TXT_CHALLENGES = "Challenges"
        private const val TXT_HEALTH = "Health"
        private const val TXT_STR = "Strength"
        private const val TXT_DURATION = "Game Duration"
        private const val TXT_DEPTH = "Maximum Depth"
        private const val TXT_ENEMIES = "Mobs Killed"
        private const val TXT_GOLD = "Gold Collected"
        private const val TXT_FOOD = "Food Eaten"
        private const val TXT_ALCHEMY = "Potions Cooked"
        private const val TXT_ANKHS = "Ankhs Used"
        private const val WIDTH = 112
        private const val HEIGHT = 134
        private const val TAB_WIDTH = 40
        private const val GAP = 4
        private const val ITEM_BUTTON_SIZE = 26
        private const val LABELLED_ITEM_BUTTON_SIZE = 26f
    }
}
