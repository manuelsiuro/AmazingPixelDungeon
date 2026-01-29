package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Window
open class WndMessage(text: String) : Window() {
    init {
        val info = PixelScene.createMultiline(text, 6f)
        info.maxWidth = (if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P) - MARGIN * 2
        info.measure()
        info.y = MARGIN.toFloat()
        info.x = info.y
        add(info)
        resize(
            (info.width() + MARGIN * 2).toInt(),
            (info.height() + MARGIN * 2).toInt()
        )
    }
    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val MARGIN = 4
    }
}
