package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Image
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.llm.LlmTextEnhancer
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Window
class WndInfoCell(cell: Int) : Window() {
    init {
        val level = Dungeon.level
        if (level != null) {
            var tile = level.map[cell]
            if (Level.water[cell]) {
                tile = Terrain.WATER
            } else if (Level.pit[cell]) {
                tile = Terrain.CHASM
            }
            val titlebar = IconTitle()
            if (tile == Terrain.WATER) {
                val waterTex = level.waterTex()
                if (waterTex != null) {
                    val water = Image(waterTex)
                    water.frame(0, 0, DungeonTilemap.SIZE, DungeonTilemap.SIZE)
                    titlebar.icon(water)
                }
            } else {
                titlebar.icon(DungeonTilemap.tile(tile))
            }
            titlebar.label(level.tileName(tile))
            titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(titlebar)
            val info = PixelScene.createMultiline(6f)
            add(info)
            val tileName = level.tileName(tile)
            val baseDesc = level.tileDesc(tile)
            val enhancedDesc = if (baseDesc.isNotEmpty()) {
                LlmTextEnhancer.enhanceCellDescription(tileName, baseDesc)
            } else {
                baseDesc
            }
            val desc = StringBuilder(enhancedDesc)
            val newLine = '\n'
            for (blob in level.blobs.values) {
                if (blob.cur[cell] > 0 && blob.tileDesc() != null) {
                    if (desc.isNotEmpty()) {
                        desc.append(newLine)
                    }
                    desc.append(blob.tileDesc())
                }
            }
            info.text(if (desc.isNotEmpty()) desc.toString() else TXT_NOTHING)
            info.maxWidth = WIDTH
            info.measure()
            info.x = titlebar.left()
            info.y = titlebar.bottom() + GAP
            resize(WIDTH, (info.y + info.height()).toInt())
        }
    }
    companion object {
        private const val GAP = 2f
        private const val WIDTH = 120
        private const val TXT_NOTHING = "There is nothing here."
    }
}
