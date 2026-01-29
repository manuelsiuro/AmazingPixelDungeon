package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
open class WndOptions(title: String, message: String, vararg options: String) : Window() {
    init {
        val tfTitle = PixelScene.createMultiline(title, 9f)
        tfTitle.hardlight(TITLE_COLOR)
        tfTitle.x = MARGIN.toFloat()
        tfTitle.y = tfTitle.x
        tfTitle.maxWidth = WIDTH - MARGIN * 2
        tfTitle.measure()
        add(tfTitle)
        val tfMessage = PixelScene.createMultiline(message, 8f)
        tfMessage.maxWidth = WIDTH - MARGIN * 2
        tfMessage.measure()
        tfMessage.x = MARGIN.toFloat()
        tfMessage.y = tfTitle.y + tfTitle.height() + MARGIN
        add(tfMessage)
        var pos = tfMessage.y + tfMessage.height() + MARGIN
        for (i in options.indices) {
            val index = i
            val btn = object : RedButton(options[i]) {
                override fun onClick() {
                    hide()
                    onSelect(index)
                }
            }
            btn.setRect(MARGIN.toFloat(), pos, (WIDTH - MARGIN * 2).toFloat(), BUTTON_HEIGHT.toFloat())
            add(btn)
            pos += BUTTON_HEIGHT + MARGIN
        }
        resize(WIDTH, pos.toInt())
    }
    protected open fun onSelect(index: Int) {}
    companion object {
        private const val WIDTH = 120
        private const val MARGIN = 2
        private const val BUTTON_HEIGHT = 20
    }
}
