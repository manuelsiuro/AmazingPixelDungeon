package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.ItemSlot
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
import kotlin.math.max
class WndItem(owner: WndBag?, item: Item) : Window() {
    init {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(item.image(), item.glowing()))
        titlebar.label(Utils.capitalize(item.toString()))
        if (item.isUpgradable && item.levelKnown) {
            titlebar.health(item.durability().toFloat() / item.maxDurability())
        }
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
        var y = info.y + info.height() + GAP
        var x = 0f
        val hero = Dungeon.hero
        if (hero != null && hero.isAlive && owner != null) {
            for (action in item.actions(hero)) {
                val btn = object : RedButton(action) {
                    override fun onClick() {
                        val h = Dungeon.hero ?: return
                        item.execute(h, action)
                        this@WndItem.hide()
                        owner.hide()
                    }
                }
                btn.setSize(max(BUTTON_WIDTH, btn.reqWidth()), BUTTON_HEIGHT)
                if (x + btn.width() > WIDTH) {
                    x = 0f
                    y += BUTTON_HEIGHT + GAP
                }
                btn.setPos(x, y)
                add(btn)
                if (action == item.defaultAction) {
                    btn.textColor(TITLE_COLOR)
                }
                x += btn.width() + GAP
            }
        }
        resize(WIDTH, (y + if (x > 0) BUTTON_HEIGHT else 0f).toInt())
    }
    companion object {
        private const val BUTTON_WIDTH = 36f
        private const val BUTTON_HEIGHT = 16f
        private const val GAP = 2f
        private const val WIDTH = 120
    }
}
