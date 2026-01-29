package com.watabou.pixeldungeon.ui
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Camera
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.scenes.PixelScene
class DangerIndicator : Tag(COLOR) {
    private lateinit var number: BitmapText
    private lateinit var icon: Image
    private var enemyIndex = 0
    private var lastNumber = -1
    init {
        setSize(24f, 16f)
        visible = false
    }
    override fun createChildren() {
        super.createChildren()
        number = BitmapText(PixelScene.font1x)
        add(number)
        icon = Icons.SKULL.get()
        add(icon)
    }
    override fun layout() {
        super.layout()
        icon.x = right() - 10
        icon.y = y + (height - icon.height) / 2
        placeNumber()
    }
    private fun placeNumber() {
        number.x = right() - 11 - number.width()
        number.y = PixelScene.align(PixelScene.uiCamera, y + (height - number.baseLine()) / 2)
    }
    override fun update() {
        val hero = Dungeon.hero ?: return
        if (hero.isAlive) {
            val v = hero.visibleEnemies()
            if (v != lastNumber) {
                lastNumber = v
                visible = lastNumber > 0
                if (visible) {
                    number.text(Integer.toString(lastNumber))
                    number.measure()
                    placeNumber()
                    flash()
                }
            }
        } else {
            visible = false
        }
        super.update()
    }
    override fun onClick() {
        val hero = Dungeon.hero ?: return
        val target = hero.visibleEnemy(enemyIndex++)
        val healthIndicator = HealthIndicator.instance
        healthIndicator?.target(if (target === healthIndicator.target()) null else target)
        val mainCamera = Camera.main ?: return
        mainCamera.target = null
        target.sprite?.let { mainCamera.focusOn(it) }
    }
    companion object {
        const val COLOR = 0xFF4C4C
    }
}
