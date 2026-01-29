package com.watabou.pixeldungeon.windows
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.scenes.RankingsScene
import com.watabou.pixeldungeon.scenes.TitleScene
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import java.io.IOException
class WndGame : Window() {
    private var pos = 0f
    init {
        addButton(object : RedButton(TXT_SETTINGS) {
            override fun onClick() {
                hide()
                GameScene.show(WndSettings(true))
            }
        })
        if (Dungeon.challenges > 0) {
            addButton(object : RedButton(TXT_CHALLEGES) {
                override fun onClick() {
                    hide()
                    GameScene.show(WndChallenges(Dungeon.challenges, false))
                }
            })
        }
        if (!Dungeon.hero!!.isAlive) {
            val btnStart = object : RedButton(TXT_START) {
                override fun onClick() {
                    Dungeon.hero = null
                    PixelDungeon.challenges(Dungeon.challenges)
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND
                    InterlevelScene.noStory = true
                    Game.switchScene(InterlevelScene::class.java)
                }
            }
            addButton(btnStart)
            btnStart.icon(Icons.get(Dungeon.hero!!.heroClass)!!)
            addButton(object : RedButton(TXT_RANKINGS) {
                override fun onClick() {
                    InterlevelScene.mode = InterlevelScene.Mode.DESCEND
                    Game.switchScene(RankingsScene::class.java)
                }
            })
        }
        addButtons(
            object : RedButton(TXT_MENU) {
                override fun onClick() {
                    try {
                        Dungeon.saveAll()
                    } catch (e: IOException) {
                        // Do nothing
                    }
                    Game.switchScene(TitleScene::class.java)
                }
            }, object : RedButton(TXT_EXIT) {
                override fun onClick() {
                    Game.instance!!.finish()
                }
            }
        )
        addButton(object : RedButton(TXT_RETURN) {
            override fun onClick() {
                hide()
            }
        })
        resize(WIDTH, pos.toInt())
    }
    private fun addButton(btn: RedButton) {
        add(btn)
        if (pos > 0) pos += GAP
        btn.setRect(0f, pos, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        pos += BTN_HEIGHT
    }
    private fun addButtons(btn1: RedButton, btn2: RedButton) {
        add(btn1)
        if (pos > 0) pos += GAP
        btn1.setRect(0f, pos, ((WIDTH - GAP) / 2).toFloat(), BTN_HEIGHT.toFloat())
        add(btn2)
        btn2.setRect(btn1.right() + GAP, btn1.top(), WIDTH - btn1.right() - GAP, BTN_HEIGHT.toFloat())
        pos += BTN_HEIGHT
    }
    companion object {
        private const val TXT_SETTINGS = "Settings"
        private const val TXT_CHALLEGES = "Challenges"
        private const val TXT_RANKINGS = "Rankings"
        private const val TXT_START = "Start New Game"
        private const val TXT_MENU = "Main Menu"
        private const val TXT_EXIT = "Exit Game"
        private const val TXT_RETURN = "Return to Game"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2
    }
}
