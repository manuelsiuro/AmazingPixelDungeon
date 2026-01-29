package com.watabou.pixeldungeon.scenes
import android.content.Intent
import android.net.Uri
import com.watabou.input.Touchscreen.Touch
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.TouchArea
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.Window
class AboutScene : PixelScene() {
    override fun create() {
        super.create()
        val text = createMultiline(TXT, 8f)
        val mainCamera = Camera.main!!
        text.maxWidth = Math.min(mainCamera.width, 120)
        text.measure()
        add(text)
        text.x = align((mainCamera.width - text.width()) / 2)
        text.y = align((mainCamera.height - text.height()) / 2)
        val link = createMultiline(LNK, 8f)
        link.maxWidth = Math.min(mainCamera.width, 120)
        link.measure()
        link.hardlight(Window.TITLE_COLOR)
        add(link)
        link.x = text.x
        link.y = text.y + text.height()
        val hotArea = object : TouchArea(link) {
            override fun onClick(touch: Touch) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://$LNK"))
                Game.instance!!.startActivity(intent)
            }
        }
        add(hotArea)
        val wata = Icons.WATA.get()
        wata.x = align((mainCamera.width - wata.width) / 2)
        wata.y = text.y - wata.height - 8f
        add(wata)
        Flare(7, 64f).color(0x112233, true).show(wata, 0f).angularSpeed = +20f
        val archs = Archs()
        archs.setSize(mainCamera.width.toFloat(), mainCamera.height.toFloat())
        addToBack(archs)
        val btnExit = ExitButton()
        btnExit.setPos(mainCamera.width - btnExit.width(), 0f)
        add(btnExit)
        fadeIn()
    }
    override fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }
    companion object {
        private const val TXT =
                "Code & graphics: Watabou\n" +
                "Music: Cube_Code\n\n" +
                "This game is inspired by Brian Walker's Brogue. " +
                "Try it on Windows, Mac OS or Linux - it's awesome! ;)\n\n" +
                "Please visit official website for additional info:"
        private const val LNK = "pixeldungeon.watabou.ru"
    }
}
