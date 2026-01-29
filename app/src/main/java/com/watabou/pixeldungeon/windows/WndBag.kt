package com.watabou.pixeldungeon.windows
import android.graphics.RectF
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.BitmapText
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.hero.Belongings
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.items.bags.Keyring
import com.watabou.pixeldungeon.items.bags.ScrollHolder
import com.watabou.pixeldungeon.items.bags.SeedPouch
import com.watabou.pixeldungeon.items.bags.WandHolster
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang
import com.watabou.pixeldungeon.plants.Plant.Seed
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.GameMath
open class WndBag(
    bag: Bag,
    private val listener: Listener?,
    private val mode: Mode?,
    private val title: String?
) : WndTabbed() {
    enum class Mode {
        ALL,
        UNIDENTIFED,
        UPGRADEABLE,
        QUICKSLOT,
        FOR_SALE,
        WEAPON,
        ARMOR,
        ENCHANTABLE,
        WAND,
        SEED
    }
    private var nCols: Int = 0
    private var nRows: Int = 0
    protected var count: Int = 0
    protected var col: Int = 0
    protected var row: Int = 0
    init {
        lastMode = mode
        lastBag = bag
        nCols = if (PixelDungeon.landscape()) COLS_L else COLS_P
        nRows = (Belongings.BACKPACK_SIZE + 4 + 1) / nCols + if ((Belongings.BACKPACK_SIZE + 4 + 1) % nCols > 0) 1 else 0
        val slotsWidth = SLOT_SIZE * nCols + SLOT_MARGIN * (nCols - 1)
        val slotsHeight = SLOT_SIZE * nRows + SLOT_MARGIN * (nRows - 1)
        val txtTitle = PixelScene.createText(title ?: Utils.capitalize(bag.name()), 9f)
        txtTitle.hardlight(TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = (slotsWidth - txtTitle.width()) / 2
        txtTitle.y = (TITLE_HEIGHT - txtTitle.height()) / 2
        add(txtTitle)
        placeItems(bag)
        resize(slotsWidth, slotsHeight + TITLE_HEIGHT)
        Dungeon.hero?.belongings?.let { stuff ->
            val bags = arrayOf<Bag?>(
                stuff.backpack,
                stuff.getItem(SeedPouch::class.java),
                stuff.getItem(ScrollHolder::class.java),
                stuff.getItem(WandHolster::class.java),
                stuff.getItem(Keyring::class.java)
            )
            for (b in bags) {
                if (b != null) {
                    val tab = BagTab(b)
                    tab.setSize(TAB_WIDTH.toFloat(), tabHeight().toFloat())
                    add(tab)
                    tab.select(b === bag)
                }
            }
        }
    }
    protected fun placeItems(container: Bag) {
        // Equipped items
        val placeStuff = Dungeon.hero?.belongings ?: return
        placeItem(placeStuff.weapon ?: Placeholder(ItemSpriteSheet.WEAPON))
        placeItem(placeStuff.armor ?: Placeholder(ItemSpriteSheet.ARMOR))
        placeItem(placeStuff.ring1 ?: Placeholder(ItemSpriteSheet.RING))
        placeItem(placeStuff.ring2 ?: Placeholder(ItemSpriteSheet.RING))
        val backpack = (container === placeStuff.backpack)
        if (!backpack) {
            count = nCols
            col = 0
            row = 1
        }
        // Items in the bag
        for (item in container.items) {
            placeItem(item)
        }
        // Free space
        while (count - (if (backpack) 4 else nCols) < container.size) {
            placeItem(null)
        }
        // Gold in the backpack
        if (container === placeStuff.backpack) {
            row = nRows - 1
            col = nCols - 1
            placeItem(Gold(Dungeon.gold))
        }
    }
    protected fun placeItem(item: Item?) {
        val x = col * (SLOT_SIZE + SLOT_MARGIN)
        val y = TITLE_HEIGHT + row * (SLOT_SIZE + SLOT_MARGIN)
        add(ItemButton(item).setPos(x.toFloat(), y.toFloat()))
        if (++col >= nCols) {
            col = 0
            row++
        }
        count++
    }
    override fun onMenuPressed() {
        if (listener == null) {
            hide()
        }
    }
    override fun onBackPressed() {
        if (listener != null) {
            listener.onSelect(null)
        }
        super.onBackPressed()
    }
    override fun onClick(tab: Tab) {
        hide()
        GameScene.show(WndBag((tab as BagTab).bag, listener, mode, title))
    }
    override fun tabHeight(): Int {
        return 20
    }
    private inner class BagTab(val bag: Bag) : Tab() {
        private val icon: Image
        init {
            icon = icon()
            add(icon)
        }
        override fun select(value: Boolean) {
            super.select(value)
            icon.am = if (selected) 1.0f else 0.6f
        }
        override fun layout() {
            super.layout()
            icon.copy(icon())
            icon.x = x + (width - icon.width) / 2
            icon.y = y + (height - icon.height) / 2 - 2 - (if (selected) 0 else 1)
            if (!selected && icon.y < y + CUT) {
                val frame = icon.frame()
                val tex = icon.texture ?: return
                frame.top += (y + CUT - icon.y) / tex.height
                icon.frame(frame)
                icon.y = y + CUT
            }
        }
        private fun icon(): Image {
            return if (bag is SeedPouch) {
                Icons.get(Icons.SEED_POUCH)
            } else if (bag is ScrollHolder) {
                Icons.get(Icons.SCROLL_HOLDER)
            } else if (bag is WandHolster) {
                Icons.get(Icons.WAND_HOLSTER)
            } else if (bag is Keyring) {
                Icons.get(Icons.KEYRING)
            } else {
                Icons.get(Icons.BACKPACK)
            }
        }
    }
    private class Placeholder(val imageRes: Int) : Item() {
        init {
            name = "Placeholder"
            image = imageRes
        }
        override val isIdentified: Boolean
            get() = true
        override fun isEquipped(hero: Hero): Boolean {
            return true
        }
    }
    private inner class ItemButton(private val item: Item?) : ItemSlot() {
        private lateinit var bg: ColorBlock
        private var durability: Array<ColorBlock>? = null

        init {
            width = SLOT_SIZE.toFloat()
            height = SLOT_SIZE.toFloat()
            item(item)
        }

        override fun createChildren() {
            // Create and add background FIRST (before super adds icon/text)
            bg = ColorBlock(SLOT_SIZE.toFloat(), SLOT_SIZE.toFloat(), NORMAL)
            add(bg)

            // Now add icon and text labels on top
            super.createChildren()
        }

        override fun layout() {
            // Position background at slot location
            bg.x = x
            bg.y = y

            // Position durability bars if present
            durability?.let { bars ->
                for (i in 0 until NBARS) {
                    bars[i].x = x + 1 + i * 3
                    bars[i].y = y + height - 3
                }
            }

            super.layout()
        }

        override fun item(item: Item?) {
            super.item(item)

            val hero = Dungeon.hero
            if (item != null && hero != null) {
                // Set background based on equipped status
                bg.texture(TextureCache.createSolid(
                    if (item.isEquipped(hero)) EQUIPPED else NORMAL
                ))

                // Cursed item indicator (reddish tint)
                if (item.cursed && item.cursedKnown) {
                    bg.ra = +0.2f
                    bg.ga = -0.1f
                } else if (!item.isIdentified) {
                    // Unidentified item indicator
                    bg.ra = 0.1f
                    bg.ba = 0.1f
                }

                // Create durability bars for upgradeable items
                val bag = lastBag
                if (bag != null && bag.owner?.isAlive == true && item.isUpgradable && item.levelKnown) {
                    durability = Array(NBARS) { i ->
                        val nBars = GameMath.gate(
                            0f,
                            Math.round(NBARS.toFloat() * item.durability() / item.maxDurability()).toFloat(),
                            NBARS.toFloat()
                        ).toInt()
                        val color = if (i < nBars) 0xFF00EE00.toInt() else 0xFFCC0000.toInt()
                        ColorBlock(2f, 2f, color).also { add(it) }
                    }
                }

                // Enable/disable based on mode
                if (item.name() == null) {
                    enable(false)
                } else {
                    enable(
                        mode == Mode.QUICKSLOT && item.defaultAction != null ||
                        mode == Mode.FOR_SALE && item.price() > 0 && (!item.isEquipped(hero) || !item.cursed) ||
                        mode == Mode.UPGRADEABLE && item.isUpgradable ||
                        mode == Mode.UNIDENTIFED && !item.isIdentified ||
                        mode == Mode.WEAPON && (item is MeleeWeapon || item is Boomerang) ||
                        mode == Mode.ARMOR && item is Armor ||
                        mode == Mode.ENCHANTABLE && (item is MeleeWeapon || item is Boomerang || item is Armor) ||
                        mode == Mode.WAND && item is Wand ||
                        mode == Mode.SEED && item is Seed ||
                        mode == Mode.ALL
                    )
                }
            } else {
                bg.color(NORMAL)
            }
        }

        override fun onTouchDown() {
            bg.brightness(1.5f)
            Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
        }

        override fun onTouchUp() {
            bg.brightness(1.0f)
        }

        override fun onClick() {
            if (listener != null) {
                hide()
                listener.onSelect(item)
            } else {
                item?.let { this@WndBag.add(WndItem(this@WndBag, it)) }
            }
        }

        override fun onLongClick(): Boolean {
            val itm = item
            if (listener == null && itm != null && itm.defaultAction != null) {
                hide()
                QuickSlot.primaryValue = if (itm.stackable) itm.javaClass else itm
                QuickSlot.refresh()
                return true
            }
            return false
        }
    }
    interface Listener {
        fun onSelect(item: Item?)
    }
    companion object {
        protected const val COLS_P = 4
        protected const val COLS_L = 6
        protected const val SLOT_SIZE = 28
        protected const val SLOT_MARGIN = 1
        protected const val TAB_WIDTH = 25
        protected const val TITLE_HEIGHT = 12
        // Colors
        private const val NORMAL = 0xFF4A4D44.toInt()
        private const val EQUIPPED = 0xFF63665B.toInt()
        private const val NBARS = 3
        private var lastMode: Mode? = null
        private var lastBag: Bag? = null
        fun lastBag(listener: Listener?, mode: Mode?, title: String?): WndBag {
            val hero = Dungeon.hero ?: throw IllegalStateException("Hero not available")
            val bag = lastBag
            return if (mode == lastMode && bag != null &&
                hero.belongings.backpack.contains(bag)
            ) {
                WndBag(bag, listener, mode, title)
            } else {
                WndBag(hero.belongings.backpack, listener, mode, title)
            }
        }
        fun seedPouch(listener: Listener?, mode: Mode?, title: String?): WndBag {
            val hero = Dungeon.hero ?: throw IllegalStateException("Hero not available")
            val pouch = hero.belongings.getItem(SeedPouch::class.java)
            return if (pouch != null)
                WndBag(pouch, listener, mode, title)
            else
                WndBag(hero.belongings.backpack, listener, mode, title)
        }
    }
}
