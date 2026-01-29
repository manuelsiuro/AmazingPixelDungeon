package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.windows.WndBag
import com.watabou.pixeldungeon.windows.WndOptions
abstract class InventoryScroll : Scroll() {
    protected var inventoryTitle = "Select an item"
    protected var mode: WndBag.Mode = WndBag.Mode.ALL
    override fun doRead() {
        if (!isKnown) {
            setKnown()
            identifiedByUse = true
        } else {
            identifiedByUse = false
        }
        GameScene.selectItem(itemSelector, mode, inventoryTitle)
    }
    private fun confirmCancelation() {
        GameScene.show(object : WndOptions(name(), TXT_WARNING, TXT_YES, TXT_NO) {
            override fun onSelect(index: Int) {
                when (index) {
                    0 -> {
                        curUser?.spendAndNext(TIME_TO_READ)
                        identifiedByUse = false
                    }
                    1 -> GameScene.selectItem(itemSelector, mode, inventoryTitle)
                }
            }
            override fun onBackPressed() {}
        })
    }
    protected abstract fun onItemSelected(item: Item)
    companion object {
        private const val TXT_WARNING = "Do you really want to cancel this scroll usage? It will be consumed anyway."
        private const val TXT_YES = "Yes, I'm positive"
        private const val TXT_NO = "No, I changed my mind"
        protected var identifiedByUse = false
        protected val itemSelector: WndBag.Listener = object : WndBag.Listener {
            override fun onSelect(item: Item?) {
                if (item != null) {
                    (curItem as? InventoryScroll)?.onItemSelected(item)
                    (curItem as? InventoryScroll)?.readAnimation()
                    Sample.play(Assets.SND_READ)
                    Invisibility.dispel()
                } else if (identifiedByUse) {
                    (curItem as? InventoryScroll)?.confirmCancelation()
                } else {
                    curItem?.collect(curUser?.belongings?.backpack)
                }
            }
        }
    }
}
