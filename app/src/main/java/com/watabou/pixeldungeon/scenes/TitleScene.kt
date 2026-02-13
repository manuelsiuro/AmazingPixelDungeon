package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Music
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.effects.BannerSprites
import com.watabou.pixeldungeon.effects.Fireball
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.PrefsButton
import android.opengl.GLES20
import javax.microedition.khronos.opengles.GL10
import kotlin.math.sin
class TitleScene : PixelScene() {
    override fun create() {
        super.create()
        Music.play(Assets.THEME, true)
        Music.volume(1f)
        uiCamera.visible = false
        val w = Camera.main!!.width
        val h = Camera.main!!.height
        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)
        val title = BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON)
        add(title)
        val height = title.height +
                if (PixelDungeon.landscape()) DashboardItem.SIZE else DashboardItem.SIZE * 2
        title.x = (w - title.width) / 2
        title.y = (h - height) / 2
        placeTorch(title.x + 18, title.y + 20)
        placeTorch(title.x + title.width - 18, title.y + 20)
        val signs = object : Image(BannerSprites.get(BannerSprites.Type.PIXEL_DUNGEON_SIGNS)) {
            private var time = 0f
            override fun update() {
                super.update()
                time += Game.elapsed
                am = sin(-time).toFloat()
            }
            override fun draw() {
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE)
                super.draw()
                GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
            }
        }
        signs.x = title.x
        signs.y = title.y
        add(signs)
        val btnBadges = object : DashboardItem(TXT_BADGES, 3) {
            override fun onClick() {
                PixelDungeon.switchNoFade(BadgesScene::class.java)
            }
        }
        add(btnBadges)
        val btnAbout = object : DashboardItem(TXT_ABOUT, 1) {
            override fun onClick() {
                PixelDungeon.switchNoFade(AboutScene::class.java)
            }
        }
        add(btnAbout)
        val btnPlay = object : DashboardItem(TXT_PLAY, 0) {
            override fun onClick() {
                PixelDungeon.switchNoFade(StartScene::class.java)
            }
        }
        add(btnPlay)
        val btnHighscores = object : DashboardItem(TXT_HIGHSCORES, 2) {
            override fun onClick() {
                PixelDungeon.switchNoFade(RankingsScene::class.java)
            }
        }
        add(btnHighscores)
        val btnGuide = object : DashboardItem(TXT_GUIDE, -1) {
            override fun createChildren() {
                super.createChildren()
                remove(image)
                val sprite = ItemSprite(ItemSpriteSheet.MASTERY, null)
                sprite.scale.set(2f)
                image = sprite
                add(image)
            }
            override fun onClick() {
                PixelDungeon.switchNoFade(EncyclopediaScene::class.java)
            }
        }
        add(btnGuide)
        if (PixelDungeon.landscape()) {
            val y = (h + height) / 2 - DashboardItem.SIZE
            val totalWidth = DashboardItem.SIZE * 5
            val startX = (w - totalWidth) / 2
            btnPlay.setPos(startX, y)
            btnHighscores.setPos(startX + DashboardItem.SIZE, y)
            btnGuide.setPos(startX + DashboardItem.SIZE * 2, y)
            btnBadges.setPos(startX + DashboardItem.SIZE * 3, y)
            btnAbout.setPos(startX + DashboardItem.SIZE * 4, y)
        } else {
            val bottomY = (h + height) / 2 - DashboardItem.SIZE
            btnBadges.setPos(w / 2 - btnBadges.width(), bottomY)
            btnAbout.setPos(w / 2f, bottomY)
            val topY = bottomY - DashboardItem.SIZE
            val totalWidth = DashboardItem.SIZE * 3
            val startX = (w - totalWidth) / 2
            btnPlay.setPos(startX, topY)
            btnHighscores.setPos(startX + DashboardItem.SIZE, topY)
            btnGuide.setPos(startX + DashboardItem.SIZE * 2, topY)
        }
        val version = BitmapText("v " + Game.version, font1x)
        version.measure()
        version.hardlight(0x888888)
        version.x = w - version.width()
        version.y = h - version.height()
        add(version)
        val btnPrefs = PrefsButton()
        btnPrefs.setPos(0f, 0f)
        add(btnPrefs)
        val btnExit = ExitButton()
        btnExit.setPos(w - btnExit.width(), 0f)
        add(btnExit)
        fadeIn()
    }
    private fun placeTorch(x: Float, y: Float) {
        val fb = Fireball()
        fb.setPos(x, y)
        add(fb)
    }
    private open class DashboardItem(text: String, private val index: Int) : Button() {
        protected lateinit var image: Image
        private lateinit var label: BitmapText
        init {
            if (index >= 0) {
                image.frame(image.texture!!.uvRect(index * IMAGE_SIZE, 0, (index + 1) * IMAGE_SIZE, IMAGE_SIZE))
            }
            label.text(text)
            label.measure()
            setSize(SIZE, SIZE)
        }
        override fun createChildren() {
            super.createChildren()
            image = Image(Assets.DASHBOARD)
            add(image)
            label = createText(9f)
            add(label)
        }
        override fun layout() {
            super.layout()
            image.x = align(x + (width - image.width()) / 2)
            image.y = align(y)
            label.x = align(x + (width - label.width()) / 2)
            label.y = align(image.y + image.height() + 2)
        }
        override fun onTouchDown() {
            image.brightness(1.5f)
            Sample.play(Assets.SND_CLICK, 1f, 1f, 0.8f)
        }
        override fun onTouchUp() {
            image.resetColor()
        }
        companion object {
            const val SIZE = 48f
            private const val IMAGE_SIZE = 32
        }
    }
    companion object {
        private const val TXT_PLAY = "Play"
        private const val TXT_HIGHSCORES = "Rankings"
        private const val TXT_BADGES = "Badges"
        private const val TXT_ABOUT = "About"
        private const val TXT_GUIDE = "Guide"
    }
}
