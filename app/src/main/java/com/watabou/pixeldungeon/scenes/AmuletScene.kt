package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.utils.Random
class AmuletScene : PixelScene() {
    private var amulet: Image? = null
    private var timer = 0f
    override fun create() {
        super.create()
        var text: BitmapTextMultiline? = null
        if (!noText) {
            val heroClass = Dungeon.hero?.heroClass?.title() ?: "adventurer"
            val victoryText = LlmTextEnhancer.generateVictoryNarration(heroClass, TXT)
            text = PixelScene.createMultiline(victoryText, 8f)
            text.maxWidth = WIDTH
            text.measure()
            add(text)
        }
        amulet = Image(Assets.AMULET)
        amulet?.let { add(it) }
        val btnExit = object : RedButton(TXT_EXIT) {
            override fun onClick() {
                Dungeon.win(ResultDescriptions.WIN)
                Dungeon.hero?.let { Dungeon.deleteGame(it.heroClass, true) }
                com.watabou.pixeldungeon.PixelDungeon.switchNoFade(if (noText) TitleScene::class.java else RankingsScene::class.java)
            }
        }
        btnExit.setSize(WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnExit)
        val btnStay = object : RedButton(TXT_STAY) {
            override fun onClick() {
                onBackPressed()
            }
        }
        btnStay.setSize(WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnStay)
        val am = amulet ?: return
        val mainCamera = Camera.main ?: return
        val height: Float
        if (noText) {
            height = am.height + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()
            am.x = PixelScene.align((mainCamera.width - am.width) / 2)
            am.y = PixelScene.align((mainCamera.height - height) / 2)
            btnExit.setPos((mainCamera.width - btnExit.width()) / 2, am.y + am.height + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)
        } else {
            val txt = text ?: return
            height = am.height + LARGE_GAP + txt.height() + LARGE_GAP + btnExit.height() + SMALL_GAP + btnStay.height()
            am.x = PixelScene.align((mainCamera.width - am.width) / 2)
            am.y = PixelScene.align((mainCamera.height - height) / 2)
            txt.x = PixelScene.align((mainCamera.width - txt.width()) / 2)
            txt.y = am.y + am.height + LARGE_GAP
            btnExit.setPos((mainCamera.width - btnExit.width()) / 2, txt.y + txt.height() + LARGE_GAP)
            btnStay.setPos(btnExit.left(), btnExit.bottom() + SMALL_GAP)
        }
        Flare(8, 48f).color(0xFFDDBB, true).show(am, 0f).angularSpeed = +30f
        fadeIn()
    }
    override fun onBackPressed() {
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
        com.watabou.pixeldungeon.PixelDungeon.switchNoFade(InterlevelScene::class.java)
    }
    override fun update() {
        super.update()
        timer -= Game.elapsed
        if (timer < 0) {
            timer = Random.Float(0.5f, 5f)
            val am = amulet ?: return
            val star = recycle(Speck::class.java) as Speck
            star.reset(0, am.x + 10.5f, am.y + 5.5f, Speck.DISCOVER)
            add(star)
        }
    }
    companion object {
        private const val TXT_EXIT = "Let's call it a day"
        private const val TXT_STAY = "I'm not done yet"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 18
        private const val SMALL_GAP = 2f
        private const val LARGE_GAP = 8f
        private const val TXT =
                "You finally hold it in your hands, the Amulet of Yendor. Using its power " +
                        "you can take over the world or bring peace and prosperity to people or whatever. " +
                        "Anyway, your life will change forever and this game will end here. " +
                        "Or you can stay a mere mortal a little longer."
        var noText = false
    }
}
