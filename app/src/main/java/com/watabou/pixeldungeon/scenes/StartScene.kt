package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Game
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.particles.BitmaskEmitter
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.GamesInProgress
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.BannerSprites
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.WndChallenges
import com.watabou.pixeldungeon.windows.WndClass
import com.watabou.pixeldungeon.windows.WndMessage
import com.watabou.pixeldungeon.windows.WndOptions
import com.watabou.utils.Callback
import java.util.HashMap
class StartScene : PixelScene() {
    private var buttonX: Float = 0.toFloat()
    private var buttonY: Float = 0.toFloat()
    private var btnLoad: GameButton? = null
    private var btnNewGame: GameButton? = null
    private var huntressUnlocked: Boolean = false
    private var unlock: Group? = null
    override fun create() {
        super.create()
        Badges.loadGlobal()
        PixelScene.uiCamera.visible = false
        val w = Camera.main!!.width
        val h = Camera.main!!.height
        val width: Float
        val height: Float
        if (PixelDungeon.landscape()) {
            width = WIDTH_L
            height = HEIGHT_L
        } else {
            width = WIDTH_P
            height = HEIGHT_P
        }
        val left = (w - width) / 2
        var top = (h - height) / 2
        val bottom = h - top
        val archs = Archs()
        archs.setSize(w.toFloat(), h.toFloat())
        add(archs)
        val title = BannerSprites.get(BannerSprites.Type.SELECT_YOUR_HERO)
        title.x = PixelScene.align((w - title.width) / 2)
        title.y = PixelScene.align(top)
        add(title)
        buttonX = left
        buttonY = bottom - BUTTON_HEIGHT
        btnNewGame = object : GameButton(TXT_NEW) {
            override fun onClick() {
                if (GamesInProgress.check(curClass!!) != null) {
                    this@StartScene.add(object : WndOptions(TXT_REALLY, TXT_WARNING, TXT_YES, TXT_NO) {
                        override fun onSelect(index: Int) {
                            if (index == 0) {
                                startNewGame()
                            }
                        }
                    })
                } else {
                    startNewGame()
                }
            }
        }
        add(btnNewGame!!)
        btnLoad = object : GameButton(TXT_LOAD) {
            override fun onClick() {
                InterlevelScene.mode = InterlevelScene.Mode.CONTINUE
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        add(btnLoad!!)
        val centralHeight = buttonY - title.y - title.height()
        val classes = arrayOf(HeroClass.WARRIOR, HeroClass.MAGE, HeroClass.ROGUE, HeroClass.HUNTRESS)
        for (cl in classes) {
            val shield = ClassShield(cl)
            shields[cl] = shield
            add(shield)
        }
        if (PixelDungeon.landscape()) {
            val shieldW = width / 4
            val shieldH = Math.min(centralHeight, shieldW)
            top = title.y + title.height + (centralHeight - shieldH) / 2
            for (i in classes.indices) {
                val shield = shields[classes[i]]
                shield!!.setRect(left + i * shieldW, top, shieldW, shieldH)
            }
            val challenge = ChallengeButton()
            challenge.setPos(
                w / 2 - challenge.width() / 2,
                top + shieldH - challenge.height() / 2
            )
            add(challenge)
        } else {
            val shieldW = width / 2
            val shieldH = Math.min(centralHeight / 2, shieldW * 1.2f)
            top = title.y + title.height() + centralHeight / 2 - shieldH
            for (i in classes.indices) {
                val shield = shields[classes[i]]
                shield!!.setRect(
                    left + i % 2 * shieldW,
                    top + i / 2 * shieldH,
                    shieldW, shieldH
                )
            }
            val challenge = ChallengeButton()
            challenge.setPos(
                w / 2 - challenge.width() / 2,
                top + shieldH - challenge.height() / 2
            )
            add(challenge)
        }
        unlock = Group()
        add(unlock!!)
        huntressUnlocked = Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_3)
        if (!huntressUnlocked) {
            val text = PixelScene.createMultiline(TXT_UNLOCK, 9f)
            text.maxWidth = width.toInt()
            text.measure()
            var pos = bottom - BUTTON_HEIGHT + (BUTTON_HEIGHT - text.height()) / 2
            for (line in text.LineSplitter().split()) {
                line.measure()
                line.hardlight(0xFFFF00)
                line.x = PixelScene.align(w / 2 - line.width() / 2)
                line.y = PixelScene.align(pos)
                unlock!!.add(line)
                pos += line.height()
            }
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main!!.width - btnExit.width(), 0f)
        add(btnExit)
        curClass = null
        updateClass(HeroClass.values()[PixelDungeon.lastClass()])
        fadeIn()
        Badges.loadingListener = object : Callback {
            override fun call() {
                if (Game.scene() === this@StartScene) {
                    PixelDungeon.switchNoFade(StartScene::class.java)
                }
            }
        }
    }
    override fun destroy() {
        Badges.saveGlobal()
        Badges.loadingListener = null
        super.destroy()
    }
    private fun updateClass(cl: HeroClass) {
        if (curClass === cl) {
            add(WndClass(cl))
            return
        }
        if (curClass != null) {
            shields[curClass]!!.highlight(false)
        }
        curClass = cl
        shields[curClass]!!.highlight(true)
        if (cl != HeroClass.HUNTRESS || huntressUnlocked) {
            unlock!!.visible = false
            val info = GamesInProgress.check(cl)
            if (info != null) {
                btnLoad!!.visible = true
                btnLoad!!.secondary(Utils.format(TXT_DPTH_LVL, info.depth, info.level), info.challenges)
                btnNewGame!!.visible = true
                btnNewGame!!.secondary(TXT_ERASE, false)
                val w = (Camera.main!!.width - GAP) / 2 - buttonX
                btnLoad!!.setRect(
                    buttonX, buttonY, w, BUTTON_HEIGHT
                )
                btnNewGame!!.setRect(
                    btnLoad!!.right() + GAP, buttonY, w, BUTTON_HEIGHT
                )
            } else {
                btnLoad!!.visible = false
                btnNewGame!!.visible = true
                btnNewGame!!.secondary(null, false)
                btnNewGame!!.setRect(buttonX, buttonY, Camera.main!!.width - buttonX * 2, BUTTON_HEIGHT)
            }
        } else {
            unlock!!.visible = true
            btnLoad!!.visible = false
            btnNewGame!!.visible = false
        }
    }
    private fun startNewGame() {
        Dungeon.hero = null
        InterlevelScene.mode = InterlevelScene.Mode.DESCEND
        if (PixelDungeon.intro()) {
            PixelDungeon.intro(false)
            Game.switchScene(IntroScene::class.java)
        } else {
            Game.switchScene(InterlevelScene::class.java)
        }
    }
    override fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }
    private open inner class GameButton(primary: String) : RedButton(primary) {
        private val secondaryText: BitmapText = createText(6f)
        init {
            secondaryText.text(null)
            add(secondaryText)
        }
        override fun layout() {
            super.layout()
            val secondaryTextStr = secondaryText.text()
            if (secondaryTextStr != null && secondaryTextStr.isNotEmpty()) {
                text.y = PixelScene.align(y + (height - text.height() - secondaryText.baseLine()) / 2)
                secondaryText.x = PixelScene.align(x + (width - secondaryText.width()) / 2)
                secondaryText.y = PixelScene.align(text.y + text.height())
            } else {
                text.y = PixelScene.align(y + (height - text.baseLine()) / 2)
            }
        }
        fun secondary(text: String?, highlighted: Boolean) {
            secondaryText.text(text)
            secondaryText.measure()
            secondaryText.hardlight(if (highlighted) SECONDARY_COLOR_H else SECONDARY_COLOR_N)
        }
    }
    private inner class ClassShield(private val cl: HeroClass) : Button() {
        private lateinit var avatar: Image
        private lateinit var name: BitmapText
        private lateinit var emitter: Emitter
        private var brightness: Float = 0.toFloat()
        private val normal: Int
        private val highlighted: Int
        override fun createChildren() {
            super.createChildren()
            avatar = Image(Assets.AVATARS)
            add(avatar)
            name = PixelScene.createText(9f)
            add(name)
            emitter = BitmaskEmitter(avatar)
            add(emitter)
        }
        // init runs after createChildren and property initializers
        init {
            val badge = cl.masteryBadge()
            if (badge != null && Badges.isUnlocked(badge)) {
                normal = MASTERY_NORMAL
                highlighted = MASTERY_HIGHLIGHTED
            } else {
                normal = BASIC_NORMAL
                highlighted = BASIC_HIGHLIGHTED
            }
            avatar.frame(cl.ordinal * WIDTH, 0, WIDTH, HEIGHT)
            avatar.scale.set(SCALE.toFloat())
            name.text(cl.title())
            name.measure()
            name.hardlight(normal)
            brightness = MIN_BRIGHTNESS
            updateBrightness()
        }
        override fun layout() {
            super.layout()
            avatar.x = PixelScene.align(x + (width - avatar.width) / 2)
            avatar.y = PixelScene.align(y + (height - avatar.height - name.height()) / 2)
            name.x = PixelScene.align(x + (width - name.width()) / 2)
            name.y = avatar.y + avatar.height() + SCALE
        }
        override fun onTouchDown() {
            emitter.revive()
            emitter.start(Speck.factory(Speck.LIGHT), 0.05f, 7)
            Sample.play(Assets.SND_CLICK, 1f, 1f, 1.2f)
            updateClass(cl)
        }
        override fun update() {
            super.update()
            if (brightness < 1.0f && brightness > MIN_BRIGHTNESS) {
                brightness -= Game.elapsed
                if (brightness <= MIN_BRIGHTNESS) {
                    brightness = MIN_BRIGHTNESS
                }
                updateBrightness()
            }
        }
        fun highlight(value: Boolean) {
            if (value) {
                brightness = 1.0f
                name.hardlight(highlighted)
            } else {
                brightness = 0.999f
                name.hardlight(normal)
            }
            updateBrightness()
        }
        private fun updateBrightness() {
            avatar.am = brightness
            avatar.rm = brightness
            avatar.bm = brightness
            avatar.gm = brightness
        }
    }
    private inner class ChallengeButton : Button() {
        private lateinit var image: Image
        override fun createChildren() {
            super.createChildren()
            image = Icons.get(if (PixelDungeon.challenges() > 0) Icons.CHALLENGE_ON else Icons.CHALLENGE_OFF)
            add(image)
        }
        // init runs after createChildren (image is ready)
        init {
            width = image.width.toFloat()
            height = image.height.toFloat()
            image.am = if (Badges.isUnlocked(Badges.Badge.VICTORY)) 1.0f else 0.5f
        }
        override fun layout() {
            super.layout()
            image.x = PixelScene.align(x)
            image.y = PixelScene.align(y)
        }
        override fun onClick() {
            if (Badges.isUnlocked(Badges.Badge.VICTORY)) {
                this@StartScene.add(object : WndChallenges(PixelDungeon.challenges(), true) {
                    override fun onBackPressed() {
                        super.onBackPressed()
                        image.copy(Icons.get(if (PixelDungeon.challenges() > 0)
                            Icons.CHALLENGE_ON
                        else
                            Icons.CHALLENGE_OFF))
                    }
                })
            } else {
                this@StartScene.add(WndMessage(TXT_WIN_THE_GAME))
            }
        }
        override fun onTouchDown() {
            Sample.play(Assets.SND_CLICK)
        }
    }
    companion object {
        private const val BUTTON_HEIGHT = 24f
        private const val GAP = 2f
        private const val TXT_LOAD = "Load Game"
        private const val TXT_NEW = "New Game"
        private const val TXT_ERASE = "Erase current game"
        private const val TXT_DPTH_LVL = "Depth: %d, level: %d"
        private const val TXT_REALLY = "Do you really want to start new game?"
        private const val TXT_WARNING = "Your current game progress will be erased."
        private const val TXT_YES = "Yes, start new game"
        private const val TXT_NO = "No, return to main menu"
        private const val TXT_UNLOCK = "To unlock this character class, slay the 3rd boss with any other class"
        private const val TXT_WIN_THE_GAME = "To unlock \"Challenges\", win the game with any character class."
        private const val WIDTH_P = 116f
        private const val HEIGHT_P = 220f
        private const val WIDTH_L = 224f
        private const val HEIGHT_L = 124f
        private const val MIN_BRIGHTNESS = 0.6f
        private const val BASIC_NORMAL = 0x444444
        private const val BASIC_HIGHLIGHTED = 0xCACFC2
        private const val MASTERY_NORMAL = 0x666644
        private const val MASTERY_HIGHLIGHTED = 0xFFFF88
        private const val WIDTH = 24
        private const val HEIGHT = 28
        private const val SCALE = 2
        private val shields = HashMap<HeroClass, ClassShield>()
        private const val SECONDARY_COLOR_N = 0xCACFC2
        private const val SECONDARY_COLOR_H = 0xFFFF88
        var curClass: HeroClass? = null
    }
}
