package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.Challenges
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.CheckBox
import com.watabou.pixeldungeon.ui.Window
open class WndChallenges(checked: Int, private val editable: Boolean) : Window() {
    private val boxes = ArrayList<CheckBox>()
    init {
        val title = PixelScene.createText(TITLE, 9f)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = PixelScene.align(camera!!, (WIDTH - title.width()) / 2)
        title.y = PixelScene.align(camera!!, (TTL_HEIGHT - title.height()) / 2)
        add(title)
        var pos = TTL_HEIGHT.toFloat()
        for (i in Challenges.NAMES.indices) {
            val cb = CheckBox(Challenges.NAMES[i])
            cb.checked((checked and Challenges.MASKS[i]) != 0)
            cb.active = editable
            if (i > 0) {
                pos += GAP
            }
            cb.setRect(0f, pos, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
            pos = cb.bottom()
            add(cb)
            boxes.add(cb)
        }
        resize(WIDTH, pos.toInt())
    }
    override fun onBackPressed() {
        if (editable) {
            var value = 0
            for (i in boxes.indices) {
                if (boxes[i].checked()) {
                    value = value or Challenges.MASKS[i]
                }
            }
            PixelDungeon.challenges(value)
        }
        super.onBackPressed()
    }
    companion object {
        private const val WIDTH = 108
        private const val TTL_HEIGHT = 12
        private const val BTN_HEIGHT = 18
        private const val GAP = 1
        private const val TITLE = "Challenges"
    }
}
