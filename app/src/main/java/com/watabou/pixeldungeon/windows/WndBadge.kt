package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.effects.BadgeBanner
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Window
class WndBadge(badge: Badges.Badge) : Window() {
    init {
        val icon = BadgeBanner.image(badge.image)
        icon.scale.set(2f)
        add(icon)
        val info = PixelScene.createMultiline(badge.description, 8f)
        info.maxWidth = (WIDTH - MARGIN * 2).toInt()
        info.measure()
        val w = Math.max(icon.width, info.width()) + MARGIN * 2
        icon.x = (w - icon.width) / 2
        icon.y = MARGIN.toFloat()
        var pos = icon.y + icon.height + MARGIN
        for (line in info.LineSplitter().split()) {
            line.measure()
            line.x = PixelScene.align((w - line.width()) / 2)
            line.y = PixelScene.align(pos)
            add(line)
            pos += line.height()
        }
        resize(w.toInt(), (pos + MARGIN).toInt())
        BadgeBanner.highlight(icon, badge.image)
    }
    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 4
    }
}
