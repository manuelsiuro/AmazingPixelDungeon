package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.effects.BadgeBanner
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.windows.WndBadge
import java.util.ArrayList
class BadgesList(global: Boolean) : ScrollPane(Component()) {
    private val items = ArrayList<ListItem>()
    init {
        for (badge in Badges.filtered(global)) {
            if (badge.image == -1) {
                continue
            }
            val item = ListItem(badge)
            content.add(item)
            items.add(item)
        }
    }
    override fun layout() {
        var pos = 0f
        for (item in items) {
            item.setRect(0f, pos, width, HEIGHT)
            pos += HEIGHT
        }
        content.setSize(width, pos)
        super.layout()
    }
    override fun onClick(x: Float, y: Float) {
        for (item in items) {
            if (item.onClick(x, y)) {
                break
            }
        }
    }
    private inner class ListItem(private val badge: Badges.Badge) : Component() {
        private lateinit var icon: Image
        private lateinit var label: BitmapText
        override fun createChildren() {
            icon = Image()
            add(icon)
            label = PixelScene.createText(6f)
            add(label)
            icon.copy(BadgeBanner.image(badge.image))
            label.text(badge.description)
        }
        override fun layout() {
            icon.x = x
            icon.y = PixelScene.align(y + (height - icon.height) / 2)
            label.x = icon.x + icon.width + 2
            label.y = PixelScene.align(y + (height - label.baseLine()) / 2)
        }
        fun onClick(x: Float, y: Float): Boolean {
            if (inside(x, y)) {
                Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
                Game.scene()?.add(WndBadge(badge))
                return true
            } else {
                return false
            }
        }
    }
    companion object {
        const val HEIGHT = 20f
    }
}
