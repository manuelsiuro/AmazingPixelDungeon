package com.watabou.pixeldungeon.items.scrolls
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.effects.Identification
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
class ScrollOfIdentify : InventoryScroll() {
    init {
        name = "Scroll of Identify"
        inventoryTitle = "Select an item to identify"
        mode = WndBag.Mode.UNIDENTIFED
    }
    override fun onItemSelected(item: Item) {
        curUser?.sprite?.let { sprite ->
            sprite.parent?.add(Identification(sprite.center().offset(0f, -16f)))
        }
        item.identify()
        GLog.i("It is $item")
        Badges.validateItemLevelAquired(item)
    }
    override fun desc(): String {
        return "Permanently reveals all of the secrets of a single item."
    }
    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
}
