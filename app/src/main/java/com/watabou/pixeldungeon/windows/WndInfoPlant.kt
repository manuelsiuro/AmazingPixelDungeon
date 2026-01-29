package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.PlantSprite
import com.watabou.pixeldungeon.ui.Window
class WndInfoPlant(plant: Plant) : Window() {
    init {
        val titlebar = IconTitle()
        titlebar.icon(PlantSprite(plant.image))
        titlebar.label(plant.plantName ?: "")
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val info = PixelScene.createMultiline(6f)
        add(info)
        info.text(plant.desc())
        info.maxWidth = WIDTH
        info.measure()
        info.x = titlebar.left()
        info.y = titlebar.bottom() + GAP
        resize(WIDTH, (info.y + info.height()).toInt())
    }
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
    }
}
