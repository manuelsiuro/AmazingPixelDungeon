package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Image
import com.watabou.noosa.NinePatch
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.scenes.PixelScene
open class RedButton(label: String) : Button() {
    protected lateinit var bg: NinePatch
    protected lateinit var text: BitmapText
    protected var icon: Image? = null
    init {
        text.text(label)
        text.measure()
    }
    override fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.BUTTON) ?: return
        add(bg)
        text = PixelScene.createText(9f)
        add(text)
    }
    override fun layout() {
        super.layout()
        bg.x = x
        bg.y = y
        bg.size(width, height)
        text.x = x + (width - text.width()) / 2
        text.y = y + (height - text.baseLine()) / 2
        val i = icon
        if (i != null) {
            i.x = x + text.x - i.width() - 2
            i.y = y + (height - i.height()) / 2
        }
    }
    override fun onTouchDown() {
        bg.brightness(1.2f)
        Sample.play(Assets.SND_CLICK)
    }
    override fun onTouchUp() {
        bg.resetColor()
    }
    fun enable(value: Boolean) {
        active = value
        text.alpha(if (value) 1.0f else 0.3f)
    }
    fun text(value: String) {
        text.text(value)
        text.measure()
        layout()
    }
    fun textColor(value: Int) {
        text.hardlight(value)
    }
    fun icon(icon: Image) {
        val oldIcon = this.icon
        if (oldIcon != null) {
            remove(oldIcon)
        }
        this.icon = icon
        val newIcon = this.icon
        if (newIcon != null) {
            add(newIcon)
            layout()
        }
    }
    fun reqWidth(): Float {
        return text.width() + 4
    }
    fun reqHeight(): Float {
        return text.baseLine() + 4
    }
}
