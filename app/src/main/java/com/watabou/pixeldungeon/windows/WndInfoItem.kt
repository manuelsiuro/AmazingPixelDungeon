package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
class WndInfoItem : Window {
    constructor(heap: Heap) : super() {
        if (heap.type == Heap.Type.HEAP || heap.type == Heap.Type.FOR_SALE) {
            val item = heap.peek() // Nullable? Usually peek() returns Item?
            var color = TITLE_COLOR
            if (item!!.levelKnown) {
                if (item.level() < 0) {
                    color = ItemSlot.DEGRADED
                } else if (item.level() > 0) {
                    color = if (item.isBroken) ItemSlot.WARNING else ItemSlot.UPGRADED
                }
            }
            fillFields(item.image(), item.glowing(), color, item.toString(), item.info())
        } else {
            var title: String
            var info: String
            if (heap.type == Heap.Type.CHEST || heap.type == Heap.Type.MIMIC) {
                title = TXT_CHEST
                info = TXT_WONT_KNOW
            } else if (heap.type == Heap.Type.TOMB) {
                title = TXT_TOMB
                info = TXT_OWNER
            } else if (heap.type == Heap.Type.SKELETON) {
                title = TXT_SKELETON
                info = TXT_REMAINS
            } else if (heap.type == Heap.Type.CRYSTAL_CHEST) {
                title = TXT_CRYSTAL_CHEST
                info = Utils.format(TXT_INSIDE, Utils.indefinite(heap.peek()!!.name()))
            } else {
                title = TXT_LOCKED_CHEST
                info = TXT_NEED_KEY
            }
            fillFields(heap.image(), heap.glowing(), TITLE_COLOR, title, info)
        }
    }
    constructor(item: Item) : super() {
        var color = TITLE_COLOR
        if (item.levelKnown) {
            if (item.level() < 0 || item.isBroken) {
                color = ItemSlot.DEGRADED
            } else if (item.level() > 0) {
                color = ItemSlot.UPGRADED
            }
        }
        fillFields(item.image(), item.glowing(), color, item.toString(), item.info())
    }
    private fun fillFields(image: Int, glowing: ItemSprite.Glowing?, titleColor: Int, title: String, info: String) {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(image, glowing))
        titlebar.label(Utils.capitalize(title), titleColor)
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val txtInfo = PixelScene.createMultiline(info, 6f)
        txtInfo.maxWidth = WIDTH
        txtInfo.measure()
        txtInfo.x = titlebar.left()
        txtInfo.y = titlebar.bottom() + GAP
        add(txtInfo)
        resize(WIDTH, (txtInfo.y + txtInfo.height()).toInt())
    }
    companion object {
        private const val TXT_CHEST = "Chest"
        private const val TXT_LOCKED_CHEST = "Locked chest"
        private const val TXT_CRYSTAL_CHEST = "Crystal chest"
        private const val TXT_TOMB = "Tomb"
        private const val TXT_SKELETON = "Skeletal remains"
        private const val TXT_WONT_KNOW = "You won't know what's inside until you open it!"
        private const val TXT_NEED_KEY = "$TXT_WONT_KNOW But to open it you need a golden key."
        private const val TXT_INSIDE = "You can see %s inside, but to open the chest you need a golden key."
        private const val TXT_OWNER =
            "This ancient tomb may contain something useful, but its owner will most certainly object to checking."
        private const val TXT_REMAINS =
            "This is all that's left from one of your predecessors. Maybe it's worth checking for any valuables."
        private const val GAP = 2f
        private const val WIDTH = 120
    }
}
