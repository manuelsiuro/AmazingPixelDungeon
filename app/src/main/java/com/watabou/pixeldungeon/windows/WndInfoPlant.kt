package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.pixeldungeon.sprites.PlantSprite
import com.watabou.pixeldungeon.ui.HighlightedText
import com.watabou.pixeldungeon.ui.Window
class WndInfoPlant(plant: Plant) : Window() {
    init {
        val titlebar = IconTitle()
        titlebar.icon(PlantSprite(plant.image))
        titlebar.label(plant.plantName ?: "")
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val info = HighlightedText(6f)
        add(info)
        val baseDesc = plant.desc() ?: ""
        info.text(LlmTextEnhancer.enhancePlantDescription(plant.plantName ?: "plant", baseDesc), WIDTH)
        info.setPos(titlebar.left(), titlebar.bottom() + GAP)
        resize(WIDTH, (info.bottom()).toInt())
    }
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
    }
}