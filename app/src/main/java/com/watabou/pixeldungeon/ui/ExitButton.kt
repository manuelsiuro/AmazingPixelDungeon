package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.TitleScene
class ExitButton : Button() {
    private lateinit var image: Image
    init {
        // Warning: width/height will be 0 here if createChildren hasn't run or if image not set.
        // But super() calls createChildren, which sets image.
        // And we use lateinit, so no overwriting.
        // So image should be initialized if super constructor finishes.
        // But we are IN init block, which runs AFTER super.
        // So image IS initialized.
        width = image.width.toFloat() // image is non-null
        height = image.height.toFloat()
    }
    override fun createChildren() {
        super.createChildren()
        image = Icons.EXIT.get()
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
        if (Game.scene() is TitleScene) {
            Game.instance?.finish()
        } else {
            PixelDungeon.switchNoFade(TitleScene::class.java)
        }
    }
}
