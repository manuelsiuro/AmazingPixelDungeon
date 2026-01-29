package com.watabou.pixeldungeon.ui
import com.watabou.noosa.Game
import com.watabou.noosa.NinePatch
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Chrome
open class Tag(color: Int) : Button() {
    private val r: Float
    private val g: Float
    private val b: Float
    protected var bg: NinePatch? = null
    protected var lightness: Float = 0f
    init {
        this.r = (color shr 16) / 255f
        this.g = ((color shr 8) and 0xFF) / 255f
        this.b = (color and 0xFF) / 255f
    }
    override fun createChildren() {
        super.createChildren()
        bg = Chrome.get(Chrome.Type.TAG)
        bg?.let { add(it) }
    }
    override fun layout() {
        super.layout()
        val background = bg ?: return
        background.x = x
        background.y = y
        background.size(width, height)
    }
    fun flash() {
        lightness = 1f
    }
    override fun update() {
        super.update()
        val background = bg ?: return
        if (visible && lightness > 0.5f) {
            lightness -= Game.elapsed
            if (lightness > 0.5f) {
                background.ba = 2 * lightness - 1
                background.ga = background.ba
                background.ra = background.ga
                background.rm = 2 * r * (1 - lightness)
                background.gm = 2 * g * (1 - lightness)
                background.bm = 2 * b * (1 - lightness)
            } else {
                background.hardlight(r, g, b)
            }
        }
    }
}
