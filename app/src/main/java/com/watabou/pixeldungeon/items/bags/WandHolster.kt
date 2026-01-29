package com.watabou.pixeldungeon.items.bags
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class WandHolster : Bag() {
    init {
        name = "wand holster"
        image = ItemSpriteSheet.HOLSTER
        size = 12
    }
    override fun grab(item: Item): Boolean {
        return item is Wand
    }
    override fun collect(container: Bag?): Boolean {
        if (super.collect(container)) {
            owner?.let { o ->
                for (item in items) {
                    (item as Wand).charge(o)
                }
            }
            return true
        } else {
            return false
        }
    }
    override fun onDetach() {
        for (item in items) {
            (item as Wand).stopCharging()
        }
    }
    override fun price(): Int {
        return 50
    }
    override fun info(): String {
        return "This slim holder is made of leather of some exotic animal. " +
                "It allows to compactly carry up to " + size + " wands."
    }
}
