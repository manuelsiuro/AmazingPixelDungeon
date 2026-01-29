package com.watabou.pixeldungeon.windows
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.ui.HighlightedText
import com.watabou.pixeldungeon.ui.Window
open class WndTitledMessage(titlebar: Component, message: String) : Window() {
    constructor(icon: Image, title: String, message: String) : this(IconTitle(icon, title), message)
    init {
        val w = if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P
        titlebar.setRect(0f, 0f, w.toFloat(), 0f)
        add(titlebar)
        val text = HighlightedText(6f)
        text.text(message, w)
        text.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(text)
        resize(w, text.bottom().toInt())
    }
    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val GAP = 2f
    }
}
