package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.utils.Highlighter
class HighlightedText(size: Float) : Component() {
    protected var normal: BitmapTextMultiline
    protected var highlighted: BitmapTextMultiline
    protected var nColor = 0xFFFFFF
    protected var hColor = 0xFFFF44
    init {
        normal = PixelScene.createMultiline(size)
        add(normal)
        highlighted = PixelScene.createMultiline(size)
        add(highlighted)
        setColor(0xFFFFFF, 0xFFFF44)
    }
    override fun layout() {
        highlighted.x = x
        normal.x = highlighted.x
        highlighted.y = y
        normal.y = highlighted.y
    }
    fun text(value: String, maxWidth: Int) {
        val hl = Highlighter(value)
        normal.text(hl.text)
        normal.maxWidth = maxWidth
        normal.measure()
        if (hl.isHighlighted()) {
            normal.mask = hl.inverted()
            highlighted.text(hl.text)
            highlighted.maxWidth = maxWidth
            highlighted.measure()
            highlighted.mask = hl.mask
            highlighted.visible = true
        } else {
            highlighted.visible = false
        }
        width = normal.width()
        height = normal.height()
    }
    fun setColor(n: Int, h: Int) {
        normal.hardlight(n)
        highlighted.hardlight(h)
    }
}
