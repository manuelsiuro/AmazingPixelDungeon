package com.watabou.pixeldungeon.ui
import com.watabou.pixeldungeon.scenes.PixelScene
open class CheckBox(label: String) : RedButton(label) {
    private var checked = false
    init {
        icon(Icons.get(Icons.UNCHECKED))
    }
    override fun layout() {
        super.layout()
        val txt = text
        val ic = icon ?: return
        var margin = (height - txt.baseLine()) / 2
        txt.x = PixelScene.align(PixelScene.uiCamera, x + margin)
        txt.y = PixelScene.align(PixelScene.uiCamera, y + margin)
        margin = (height - ic.height) / 2
        ic.x = PixelScene.align(PixelScene.uiCamera, x + width - margin - ic.width)
        ic.y = PixelScene.align(PixelScene.uiCamera, y + margin)
    }
    fun checked(): Boolean {
        return checked
    }
    fun checked(value: Boolean) {
        if (checked != value) {
            checked = value
            icon?.copy(Icons.get(if (checked) Icons.CHECKED else Icons.UNCHECKED))
        }
    }
    override fun onClick() {
        super.onClick()
        checked(!checked)
    }
}
