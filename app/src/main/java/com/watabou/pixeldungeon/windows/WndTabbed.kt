package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Game
import com.watabou.noosa.NinePatch
import com.watabou.noosa.audio.Sample
import com.watabou.noosa.ui.Button
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Chrome
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.Window
import java.util.ArrayList
open class WndTabbed : Window {
    val tabs = ArrayList<Tab>()
    var selected: Tab? = null
    constructor() : super(0, 0, Chrome.get(Chrome.Type.TAB_SET) ?: throw IllegalStateException("Chrome tab set not available"))
    protected fun add(tab: Tab): Tab {
        tab.setPos(
            if (tabs.isEmpty()) -chrome.marginLeft() + 1f else tabs[tabs.size - 1].right(),
            height.toFloat()
        )
        tab.select(false)
        super.add(tab)
        tabs.add(tab)
        return tab
    }
    fun select(index: Int) {
        select(tabs[index])
    }
    protected open fun select(tab: Tab) {
        if (tab !== selected) {
            for (t in tabs) {
                if (t === selected) {
                    t.select(false)
                } else if (t === tab) {
                    t.select(true)
                }
            }
            selected = tab
        }
    }
    override fun resize(w: Int, h: Int) {
        width = w
        height = h
        chrome.size(
            width + chrome.marginHor().toFloat(),
            height + chrome.marginVer().toFloat()
        )
        val c = camera ?: return
        c.resize(
            chrome.width().toInt(),
            (chrome.marginTop() + height + tabHeight()).toInt()
        )
        c.x = (Game.width - c.screenWidth()).toInt() / 2
        c.y = (Game.height - c.screenHeight()).toInt() / 2
        shadow.boxRect(
            c.x / c.zoom,
            c.y / c.zoom,
            chrome.width(), chrome.height()
        )
        val tempTabs = ArrayList(tabs)
        tabs.clear()
        for (tab in tempTabs) {
            add(tab)
        }
    }
    protected open fun tabHeight(): Int {
        return 25
    }
    protected open fun onClick(tab: Tab) {
        select(tab)
    }
    open inner class Tab : Button() {
        val CUT = 5
        var selected: Boolean = false
        var bg: NinePatch? = null
        override fun layout() {
            super.layout()
            bg?.let {
                it.x = x
                it.y = y
                it.size(width, height)
            }
        }
        open fun select(value: Boolean) {
            selected = value
            active = !value
            bg?.let { remove(it) }
            bg = Chrome.get(if (selected) Chrome.Type.TAB_SELECTED else Chrome.Type.TAB_UNSELECTED)
            bg?.let { addToBack(it) }
            layout()
        }
        override fun onClick() {
            Sample.play(Assets.SND_CLICK, 0.7f, 0.7f, 1.2f)
            this@WndTabbed.onClick(this)
        }
    }
    open inner class LabeledTab(label: String) : Tab() {
        private val btLabel: BitmapText = PixelScene.createText(9f).also {
            add(it)
            it.text(label)
            it.measure()
        }
        // Skip createChildren to avoid initialization order issues, 
        // logic moved to init block.
        // Component constructor calls createChildren but we do nothing there.
        override fun layout() {
            super.layout()
            btLabel.x = PixelScene.align(x + (width - btLabel.width()) / 2)
            btLabel.y = PixelScene.align(y + (height - btLabel.baseLine()) / 2) - 1
            if (!selected) {
                btLabel.y -= 2f
            }
        }
        override fun select(value: Boolean) {
            super.select(value)
            btLabel.am = if (selected) 1.0f else 0.6f
        }
    }
}
