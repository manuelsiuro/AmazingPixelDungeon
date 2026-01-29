package com.watabou.pixeldungeon.items.bags
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class ScrollHolder : Bag() {
    init {
        name = "scroll holder"
        image = ItemSpriteSheet.HOLDER
        size = 12
    }
    override fun grab(item: Item): Boolean {
        return item is Scroll
    }
    override fun price(): Int {
        return 50
    }
    override fun info(): String {
        return "You can place any number of scrolls into this tubular container. " +
                "It saves room in your backpack and protects scrolls from fire."
    }
}
