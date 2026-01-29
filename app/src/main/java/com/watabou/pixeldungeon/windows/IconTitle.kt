package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.HealthBar
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
import kotlin.math.max
class IconTitle : Component {
    protected lateinit var imIcon: Image
    protected lateinit var tfLabel: BitmapTextMultiline
    protected lateinit var health: HealthBar
    private var healthLvl = Float.NaN
    constructor() : super()
    constructor(item: Item) : this(
        ItemSprite(item.image(), item.glowing()),
        Utils.capitalize(item.toString())
    )
    constructor(icon: Image?, label: String) : super() {
        if (icon != null) icon(icon)
        label(label)
    }
    override fun createChildren() {
        imIcon = Image()
        add(imIcon)
        tfLabel = PixelScene.createMultiline(FONT_SIZE)
        tfLabel.hardlight(Window.TITLE_COLOR)
        add(tfLabel)
        health = HealthBar()
        add(health)
    }
    override fun layout() {
        health.visible = !healthLvl.isNaN()
        imIcon.x = x
        imIcon.y = y
        tfLabel.x = PixelScene.align(PixelScene.uiCamera, imIcon.x + imIcon.width() + GAP)
        tfLabel.maxWidth = (width - tfLabel.x).toInt()
        tfLabel.measure()
        tfLabel.y = PixelScene.align(
            PixelScene.uiCamera,
            if (imIcon.height > tfLabel.height())
                imIcon.y + (imIcon.height() - tfLabel.baseLine()) / 2
            else
                imIcon.y
        )
        if (health.visible) {
            health.setRect(
                tfLabel.x,
                max(tfLabel.y + tfLabel.height(), imIcon.y + imIcon.height() - health.height()),
                tfLabel.maxWidth.toFloat(),
                0f
            )
            height = health.bottom()
        } else {
            height = max(imIcon.y + imIcon.height(), tfLabel.y + tfLabel.height())
        }
    }
    fun icon(icon: Image?) {
        if (icon != null) {
            remove(imIcon)
            imIcon = icon
            add(imIcon)
        }
    }
    fun label(label: String) {
        tfLabel.text(label)
    }
    fun label(label: String, color: Int) {
        tfLabel.text(label)
        tfLabel.hardlight(color)
    }
    fun color(color: Int) {
        tfLabel.hardlight(color)
    }
    fun health(value: Float) {
        healthLvl = value
        health.level(healthLvl)
        layout()
    }
    companion object {
        private const val FONT_SIZE = 9f
        private const val GAP = 2f
    }
}
