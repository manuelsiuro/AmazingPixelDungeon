package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.mobs.npcs.NPC
import com.watabou.pixeldungeon.ui.HighlightedText
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
open class WndQuest(questgiver: NPC, text: String, vararg options: String) : Window() {
    init {
        val width = if (PixelDungeon.landscape()) WIDTH_L else WIDTH_P
        val titlebar = IconTitle(questgiver.sprite(), Utils.capitalize(questgiver.name))
        titlebar.setRect(0f, 0f, width.toFloat(), 0f)
        add(titlebar)
        val hl = HighlightedText(6f)
        hl.text(text, width)
        hl.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(hl)
        if (options.isNotEmpty()) {
            var pos = hl.bottom()
            for (i in options.indices) {
                pos += GAP
                val index = i
                val btn = object : RedButton(options[i]) {
                    override fun onClick() {
                        hide()
                        onSelect(index)
                    }
                }
                btn.setRect(0f, pos, width.toFloat(), BTN_HEIGHT.toFloat())
                add(btn)
                pos += BTN_HEIGHT
            }
            resize(width, pos.toInt())
        } else {
            resize(width, hl.bottom().toInt())
        }
    }
    protected open fun onSelect(index: Int) {}
    companion object {
        private const val WIDTH_P = 120
        private const val WIDTH_L = 144
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }
}
