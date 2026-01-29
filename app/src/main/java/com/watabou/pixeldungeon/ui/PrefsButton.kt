package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.windows.WndSettings
class PrefsButton : Button() {
    private lateinit var image: Image
    init {
        width = image.width.toFloat()
        height = image.height.toFloat()
    }
    override fun createChildren() {
        super.createChildren()
        image = Icons.PREFS.get()
        add(image)
    }
    override fun layout() {
        super.layout()
        image.x = x
        image.y = y
    }
    override fun onTouchDown() {
        image.brightness(1.5f)
        Sample.play(Assets.SND_CLICK)
    }
    override fun onTouchUp() {
        image.resetColor()
    }
    override fun onClick() {
        parent?.add(WndSettings(false))
    }
}
