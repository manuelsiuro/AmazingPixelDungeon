package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Game
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.scenes.PixelScene
class GoldIndicator : Component() {
    private var lastValue = 0
    private lateinit var tf: BitmapText
    private var time: Float = 0f
    override fun createChildren() {
        tf = BitmapText(PixelScene.font1x)
        tf.hardlight(0xFFFF00)
        add(tf)
        visible = false
    }
    override fun layout() {
        tf.x = x + (width - tf.width()) / 2
        tf.y = bottom() - tf.height()
    }
    override fun update() {
        super.update()
        if (visible) {
            time -= Game.elapsed
            if (time > 0) {
                tf.alpha(if (time > TIME / 2) 1f else time * 2 / TIME)
            } else {
                visible = false
            }
        }
        if (Dungeon.gold != lastValue) {
            lastValue = Dungeon.gold
            tf.text(Integer.toString(lastValue))
            tf.measure()
            visible = true
            time = TIME
            layout()
        }
    }
    companion object {
        private const val TIME = 2f
    }
}
