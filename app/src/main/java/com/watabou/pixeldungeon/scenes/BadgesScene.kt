package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.effects.BadgeBanner
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.windows.WndBadge
import com.watabou.utils.Callback
import com.watabou.utils.Random
class BadgesScene : PixelScene() {
    override fun create() {
        super.create()
        Music.play(Assets.THEME, true)
        Music.volume(1f)
        PixelScene.uiCamera.visible = false
        val w = Camera.main!!.width
        val h = Camera.main!!.height
        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)
        val pw = (Math.min(w.toFloat(), (if (PixelDungeon.landscape()) MIN_WIDTH_L else MIN_WIDTH_P) * 3) - 16).toInt()
        val ph = (Math.min(h.toFloat(), (if (PixelDungeon.landscape()) MIN_HEIGHT_L else MIN_HEIGHT_P) * 3) - 32).toInt()
        var size = Math.sqrt((pw * ph / 27f).toDouble()).toFloat()
        val nCols = Math.ceil((pw / size).toDouble()).toInt()
        val nRows = Math.ceil((ph / size).toDouble()).toInt()
        size = Math.min((pw / nCols).toFloat(), (ph / nRows).toFloat())
        val left = (w - size * nCols) / 2
        val top = (h - size * nRows) / 2
        val title = PixelScene.createText(TXT_TITLE, 9f)
        title.hardlight(Window.TITLE_COLOR)
        title.measure()
        title.x = PixelScene.align((w - title.width()) / 2)
        title.y = PixelScene.align((top - title.baseLine()) / 2)
        add(title)
        Badges.loadGlobal()
        val badges = Badges.filtered(true)
        for (i in 0 until nRows) {
            for (j in 0 until nCols) {
                val index = i * nCols + j
                val b = if (index < badges.size) badges[index] else null
                val button = BadgeButton(b)
                button.setPos(
                        left + j * size + (size - button.width()) / 2,
                        top + i * size + (size - button.height()) / 2)
                add(button)
            }
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main!!.width - btnExit.width(), 0f)
        add(btnExit)
        fadeIn()
        Badges.loadingListener = object : Callback {
            override fun call() {
                if (Game.scene() === this@BadgesScene) {
                    PixelDungeon.switchNoFade(BadgesScene::class.java)
                }
            }
        }
    }
    override fun destroy() {
        Badges.saveGlobal()
        Badges.loadingListener = null
        super.destroy()
    }
    override fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }
    private class BadgeButton(badge: Badges.Badge?) : Button() {
        private val badge: Badges.Badge?
        private var icon: Image? = null
        init {
            this.badge = badge
            active = (badge != null)
            icon = if (active) BadgeBanner.image(badge!!.image) else Image(Assets.LOCKED)
            add(icon!!)
            setSize(icon!!.width, icon!!.height)
        }
        override fun layout() {
            super.layout()
            icon!!.x = PixelScene.align(x + (width - icon!!.width) / 2)
            icon!!.y = PixelScene.align(y + (height - icon!!.height) / 2)
        }
        override fun update() {
            super.update()
            if (Random.Float() < Game.elapsed * 0.1) {
                BadgeBanner.highlight(icon!!, badge!!.image)
            }
        }
        override fun onClick() {
            Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            Game.scene()!!.add(WndBadge(badge!!))
        }
    }
    companion object {
        private const val TXT_TITLE = "Your Badges"
    }
}
