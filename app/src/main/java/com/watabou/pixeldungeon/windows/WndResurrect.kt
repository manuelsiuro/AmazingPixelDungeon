package com.watabou.pixeldungeon.windows
import com.watabou.noosa.Game
import com.watabou.pixeldungeon.Rankings
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Ankh
import com.watabou.pixeldungeon.scenes.InterlevelScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
class WndResurrect(ankh: Ankh, causeOfDeath: Any?) : Window() {
    init {
        instance = this
        WndResurrect.causeOfDeath = causeOfDeath
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(ankh.image(), null))
        titlebar.label(ankh.name())
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val message = PixelScene.createMultiline(TXT_MESSAGE, 6f)
        message.maxWidth = WIDTH
        message.measure()
        message.y = titlebar.bottom() + GAP
        add(message)
        val btnYes = object : RedButton(TXT_YES) {
            override fun onClick() {
                hide()
                Statistics.ankhsUsed++
                InterlevelScene.mode = InterlevelScene.Mode.RESURRECT
                Game.switchScene(InterlevelScene::class.java)
            }
        }
        btnYes.setRect(0f, message.y + message.height() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnYes)
        val btnNo = object : RedButton(TXT_NO) {
            override fun onClick() {
                hide()
                Rankings.submit(false)
                Hero.reallyDie(WndResurrect.causeOfDeath)
            }
        }
        btnNo.setRect(0f, btnYes.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnNo)
        resize(WIDTH, btnNo.bottom().toInt())
    }
    override fun destroy() {
        super.destroy()
        instance = null
    }
    override fun onBackPressed() {
    }
    companion object {
        private const val TXT_MESSAGE = "You died, but you were given another chance to win this dungeon. Will you take it?"
        private const val TXT_YES = "Yes, I will fight!"
        private const val TXT_NO = "No, I give up"
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 20
        private const val GAP = 2f
        var instance: WndResurrect? = null
        var causeOfDeath: Any? = null
    }
}
