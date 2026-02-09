package com.watabou.pixeldungeon.scenes
import com.watabou.noosa.BitmapText
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.noosa.audio.Music
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.Rankings
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.Archs
import com.watabou.pixeldungeon.ui.ExitButton
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.windows.WndError
import com.watabou.pixeldungeon.windows.WndRanking
class RankingsScene : PixelScene() {
    private var archs: Archs? = null
    override fun create() {
        super.create()
        Music.play(Assets.THEME, true)
        Music.volume(1f)
        PixelScene.uiCamera.visible = false
        val w = Camera.main!!.width
        val h = Camera.main!!.height
        archs = Archs()
        archs!!.setSize(w.toFloat(), h.toFloat())
        add(archs!!)
        Rankings.load()
        if (Rankings.records!!.size > 0) {
            val rowHeight = if (PixelDungeon.landscape()) ROW_HEIGHT_L else ROW_HEIGHT_P
            val left = (w - Math.min(MAX_ROW_WIDTH, w.toFloat())) / 2 + GAP
            val top = PixelScene.align((h - rowHeight * Rankings.records!!.size) / 2)
            val title = PixelScene.createText(TXT_TITLE, 9f)
            title.hardlight(Window.TITLE_COLOR)
            title.measure()
            title.x = PixelScene.align((w - title.width()) / 2)
            title.y = PixelScene.align(top - title.height() - GAP)
            add(title)
            var pos = 0
            for (rec in Rankings.records!!) {
                val row = Record(pos, pos == Rankings.lastRecord, rec)
                row.setRect(left, top + pos * rowHeight, w - left * 2, rowHeight)
                add(row)
                pos++
            }
            if (Rankings.totalNumber >= Rankings.TABLE_SIZE) {
                val label = PixelScene.createText(TXT_TOTAL, 8f)
                label.hardlight(DEFAULT_COLOR)
                label.measure()
                add(label)
                val won = PixelScene.createText(Integer.toString(Rankings.wonNumber), 8f)
                won.hardlight(Window.TITLE_COLOR)
                won.measure()
                add(won)
                val total = PixelScene.createText("/" + Rankings.totalNumber, 8f)
                total.hardlight(DEFAULT_COLOR)
                total.measure()
                total.x = PixelScene.align((w - total.width()) / 2)
                total.y = PixelScene.align(top + pos * rowHeight + GAP)
                add(total)
                val tw = label.width() + won.width() + total.width()
                label.x = PixelScene.align((w - tw) / 2)
                won.x = label.x + label.width()
                total.x = won.x + won.width()
                total.y = PixelScene.align(top + pos * rowHeight + GAP)
                won.y = total.y
                label.y = won.y
            }
        } else {
            val title = PixelScene.createText(TXT_NO_GAMES, 8f)
            title.hardlight(DEFAULT_COLOR)
            title.measure()
            title.x = PixelScene.align((w - title.width()) / 2)
            title.y = PixelScene.align((h - title.height()) / 2)
            add(title)
        }
        val btnExit = ExitButton()
        btnExit.setPos(Camera.main!!.width - btnExit.width(), 0f)
        add(btnExit)
        fadeIn()
    }
    override fun onBackPressed() {
        PixelDungeon.switchNoFade(TitleScene::class.java)
    }
    class Record(pos: Int, latest: Boolean, rec: Rankings.Record) : Button() {
        private val rec: Rankings.Record = rec
        private var shield: ItemSprite? = null
        private var flare: Flare? = null
        private var position: BitmapText? = null
        private var desc: BitmapTextMultiline? = null
        private var classIcon: Image? = null
        private val _pos = pos
        private var _latest = latest
        override fun createChildren() {
             super.createChildren()
             shield = ItemSprite(ItemSpriteSheet.TOMB, null)
             add(shield!!)
             position = BitmapText(PixelScene.font1x)
             add(position!!)
             desc = PixelScene.createMultiline(9f)
             add(desc!!)
             classIcon = Image()
             add(classIcon!!)
        }
        init {
             position!!.text(Integer.toString(_pos + 1))
             position!!.measure()
             desc!!.text(trimForRow(rec.info))
             desc!!.measure()
             if (rec.win) {
                 shield!!.view(ItemSpriteSheet.AMULET, null)
                 position!!.hardlight(TEXT_WIN)
                 desc!!.hardlight(TEXT_WIN)
             } else {
                 position!!.hardlight(TEXT_LOSE)
                 desc!!.hardlight(TEXT_LOSE)
             }
             classIcon!!.copy(Icons.get(rec.heroClass)!!)
             if (_latest) {
                flare = Flare(6, 24f)
                flare!!.angularSpeed = 90f
                flare!!.color(if (rec.win) FLARE_WIN else FLARE_LOSE)
                addToBack(flare!!)
            }
        }
        override fun layout() {
            super.layout()
            shield!!.x = x
            shield!!.y = y + (height - shield!!.height) / 2
            position!!.x = PixelScene.align(shield!!.x + (shield!!.width - position!!.width()) / 2)
            position!!.y = PixelScene.align(shield!!.y + (shield!!.height - position!!.height()) / 2 + 1)
            if (flare != null) {
                flare!!.point(shield!!.center())
            }
            classIcon!!.x = PixelScene.align(x + width - classIcon!!.width)
            classIcon!!.y = shield!!.y
            desc!!.x = shield!!.x + shield!!.width + GAP
            desc!!.maxWidth = (classIcon!!.x - desc!!.x).toInt()
            desc!!.measure()
            desc!!.y = position!!.y + position!!.baseLine() - desc!!.baseLine()
        }
        override fun onClick() {
            if (rec.gameFile.length > 0) {
                parent!!.add(WndRanking(rec.gameFile))
            } else {
                parent!!.add(WndError(TXT_NO_INFO))
            }
        }
        companion object {
            private const val GAP = 4f
            private const val TEXT_WIN = 0xFFFF88
            private const val TEXT_LOSE = 0xCCCCCC
            private const val FLARE_WIN = 0x888866
            private const val FLARE_LOSE = 0x666666
            private const val MAX_DESC_LENGTH = 80

            private fun trimForRow(text: String?): String {
                if (text == null) return ""
                if (text.length <= MAX_DESC_LENGTH) return text
                val truncated = text.substring(0, MAX_DESC_LENGTH)
                val lastSpace = truncated.lastIndexOf(' ')
                return if (lastSpace > MAX_DESC_LENGTH / 2) {
                    truncated.substring(0, lastSpace) + "..."
                } else {
                    "$truncated..."
                }
            }
        }
    }
    companion object {
        private const val DEFAULT_COLOR = 0xCCCCCC
        private const val TXT_TITLE = "Top Rankings"
        private const val TXT_TOTAL = "Games played: "
        private const val TXT_NO_GAMES = "No games have been played yet."
        private const val TXT_NO_INFO = "No additional information"
        private const val ROW_HEIGHT_L = 30f
        private const val ROW_HEIGHT_P = 42f
        private const val MAX_ROW_WIDTH = 180f
        private const val GAP = 4f
    }
}
