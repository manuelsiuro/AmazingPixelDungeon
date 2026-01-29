package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Shopkeeper
import com.watabou.pixeldungeon.items.EquipableItem
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.rings.RingOfHaggler
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
class WndTradeItem : Window {
    private var owner: WndBag? = null
    constructor(item: Item, owner: WndBag) : super() {
        this.owner = owner
        var pos = createDescription(item, false)
        if (item.quantity() == 1) {
            val btnSell = object : RedButton(Utils.format(TXT_SELL, item.price())) {
                override fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSell.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSell)
            pos = btnSell.bottom()
        } else {
            val priceAll = item.price()
            val btnSell1 = object : RedButton(Utils.format(TXT_SELL_1, priceAll / item.quantity())) {
                override fun onClick() {
                    sellOne(item)
                    hide()
                }
            }
            btnSell1.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSell1)
            val btnSellAll = object : RedButton(Utils.format(TXT_SELL_ALL, priceAll)) {
                override fun onClick() {
                    sell(item)
                    hide()
                }
            }
            btnSellAll.setRect(0f, btnSell1.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnSellAll)
            pos = btnSellAll.bottom()
        }
        val btnCancel = object : RedButton(TXT_CANCEL) {
            override fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom().toInt())
    }
    constructor(heap: Heap, canBuy: Boolean) : super() {
        val item = heap.peek() ?: return
        val pos = createDescription(item, true)
        if (canBuy) {
            val price = price(item)
            val btnBuy = object : RedButton(Utils.format(TXT_BUY, price)) {
                override fun onClick() {
                    hide()
                    if (heap.type == Heap.Type.FOR_SALE) {
                        buy(heap)
                    }
                }
            }
            btnBuy.setRect(0f, pos + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            btnBuy.enable(price <= Dungeon.gold)
            add(btnBuy)
            val btnCancel = object : RedButton(TXT_CANCEL) {
                override fun onClick() {
                    hide()
                }
            }
            btnCancel.setRect(0f, btnBuy.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            add(btnCancel)
            resize(WIDTH, btnCancel.bottom().toInt())
        } else {
            resize(WIDTH, pos.toInt())
        }
    }
    override fun hide() {
        super.hide()
        owner?.let {
            it.hide()
            Shopkeeper.sell()
        }
    }
    private fun createDescription(item: Item, forSale: Boolean): Float {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item.image(), item.glowing()))
        titlebar.label(
            if (forSale)
                Utils.format(TXT_SALE, item.toString(), price(item))
            else
                Utils.capitalize(item.toString())
        )
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        if (item.levelKnown) {
            if (item.level() < 0) {
                titlebar.color(ItemSlot.DEGRADED)
            } else if (item.level() > 0) {
                titlebar.color(if (item.isBroken) ItemSlot.WARNING else ItemSlot.UPGRADED)
            }
        }
        val info = PixelScene.createMultiline(item.info(), 6f)
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        add(info)
        return info.y + info.height()
    }
    private fun sell(item: Item) {
        val hero = Dungeon.hero
        // Use local variable to smart cast or check nullability if needed, though hero is likely non-null here
        // item must be equipable to call doUnequip?
        val h = hero ?: return
        if (item.isEquipped(h) && item is EquipableItem && !item.doUnequip(h, false)) {
            return
        }
        item.detachAll(h.belongings.backpack)
        val price = item.price()
        Gold(price).doPickUp(hero)
        GLog.i(TXT_SOLD, item.name(), price)
    }
    private fun sellOne(item: Item) {
        var itm = item
        if (itm.quantity() <= 1) {
            sell(itm)
        } else {
            val hero = Dungeon.hero
            val h = hero ?: return
            itm = itm.detach(h.belongings.backpack) ?: return
            val price = itm.price()
            Gold(price).doPickUp(hero)
            GLog.i(TXT_SOLD, itm.name(), price)
        }
    }
    private fun price(item: Item): Int {
        var price = item.price() * 5 * (Dungeon.depth / 5 + 1)
        if (Dungeon.hero?.buff(RingOfHaggler.Haggling::class.java) != null && price >= 2) {
            price /= 2
        }
        return price
    }
    private fun buy(heap: Heap) {
        val hero = Dungeon.hero
        val item = heap.pickUp()
        val price = price(item)
        Dungeon.gold -= price
        GLog.i(TXT_BOUGHT, item.name(), price)
        val h = hero ?: return
        if (!item.doPickUp(h)) {
            Dungeon.level?.drop(item, heap.pos)?.sprite?.drop()
        }
    }
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 16
        private const val TXT_SALE = "FOR SALE: %s - %dg"
        private const val TXT_BUY = "Buy for %dg"
        private const val TXT_SELL = "Sell for %dg"
        private const val TXT_SELL_1 = "Sell 1 for %dg"
        private const val TXT_SELL_ALL = "Sell all for %dg"
        private const val TXT_CANCEL = "Never mind"
        private const val TXT_SOLD = "You've sold your %s for %dg"
        private const val TXT_BOUGHT = "You've bought %s for %dg"
    }
}
