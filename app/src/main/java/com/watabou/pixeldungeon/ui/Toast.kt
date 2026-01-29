package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.NinePatch
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.scenes.PixelScene
open class Toast(textStr: String) : Component() {
    protected lateinit var bg: NinePatch
    protected lateinit var close: SimpleButton
    protected lateinit var text: BitmapText
    // In Java constructor called text(text).
    // In Kotlin, init runs.
    // BUT we need `text` (BitmapText) to be initialized before calling `text(String)`.
    // `text(String)` method uses `this.text` property.
    // `this.text` (BitmapText) is created in `createChildren`.
    // And `createChildren` is called in `super()`.
    // So `text` property should be initialized.
    init {
        text(textStr)
        // width and height set
        width = text.width() + close.width() + bg.marginHor() + MARGIN_HOR * 3
        height = Math.max(text.height(), close.height()) + bg.marginVer() + MARGIN_VER * 2
    }
    override fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.TOAST_TR) ?: return
        add(bg)
        close = object : SimpleButton(Icons.CLOSE.get()) {
            override fun onClick() {
                onClose()
            }
        }
        add(close)
        text = PixelScene.createText(8f)
        add(text)
    }
    override fun layout() {
        super.layout()
        bg.x = x
        bg.y = y
        bg.size(width, height)
        close.setPos(
            bg.x + bg.width() - bg.marginHor() / 2 - MARGIN_HOR - close.width(),
            y + (height - close.height()) / 2
        )
        text.x = close.left() - MARGIN_HOR - text.width()
        text.y = y + (height - text.height()) / 2
        PixelScene.align(text)
    }
    fun text(txt: String) {
        text.text(txt)
        text.measure()
    }
    protected open fun onClose() {}
    companion object {
        private const val MARGIN_HOR = 2f
        private const val MARGIN_VER = 2f
    }
}
