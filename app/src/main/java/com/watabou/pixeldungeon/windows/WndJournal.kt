package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Image
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.PixelDungeon
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Icons
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import java.util.Collections
class WndJournal : Window() {
    private val txtTitle: BitmapText
    private val list: ScrollPane
    init {
        resize(WIDTH, if (PixelDungeon.landscape()) HEIGHT_L else HEIGHT_P)
        txtTitle = PixelScene.createText(TXT_TITLE, 9f)
        txtTitle.hardlight(TITLE_COLOR)
        txtTitle.measure()
        txtTitle.x = PixelScene.align(PixelScene.uiCamera, (WIDTH - txtTitle.width()) / 2)
        add(txtTitle)
        val content = Component()
        Collections.sort(Journal.records)
        var pos = 0f
        for (rec in Journal.records) {
            val item = ListItem(rec.feature!!, rec.depth)
            item.setRect(0f, pos, WIDTH.toFloat(), ITEM_HEIGHT.toFloat())
            content.add(item)
            pos += item.height()
        }
        content.setSize(WIDTH.toFloat(), pos)
        list = ScrollPane(content)
        add(list)
        list.setRect(0f, txtTitle.height(), WIDTH.toFloat(), height - txtTitle.height())
    }
    private class ListItem(f: Journal.Feature, d: Int) : Component() {
        private lateinit var feature: BitmapText
        private lateinit var depth: BitmapText
        private lateinit var icon: Image
        private val featureDesc = f.desc
        private val depthVal = d
        override fun createChildren() {
            feature = PixelScene.createText(9f)
            add(feature)
            depth = BitmapText(PixelScene.font1x)
            add(depth)
            icon = Icons.get(Icons.DEPTH)
            add(icon)
            // Logic moved from constructor
            feature.text(featureDesc)
            feature.measure()
            depth.text(Integer.toString(depthVal))
            depth.measure()
            if (depthVal == Dungeon.depth) {
                feature.hardlight(TITLE_COLOR)
                depth.hardlight(TITLE_COLOR)
            }
        }
        override fun layout() {
            icon.x = width - icon.width
            depth.x = icon.x - 1 - depth.width()
            depth.y = PixelScene.align(y + (height - depth.height()) / 2)
            icon.y = depth.y - 1
            feature.y = PixelScene.align(depth.y + depth.baseLine() - feature.baseLine())
        }
    }
    companion object {
        private const val WIDTH = 112
        private const val HEIGHT_P = 160
        private const val HEIGHT_L = 144
        private const val ITEM_HEIGHT = 18
        private const val TXT_TITLE = "Journal"
    }
}
