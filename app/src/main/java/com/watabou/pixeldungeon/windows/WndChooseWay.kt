package com.watabou.pixeldungeon.windows
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.items.TomeOfMastery
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.HighlightedText
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.Utils
class WndChooseWay : Window {
    constructor(tome: TomeOfMastery, way1: HeroSubClass, way2: HeroSubClass) : super() {
        val TXT_MASTERY = "Which way will you follow?"
        val TXT_CANCEL = "I'll decide later"
        val bottom = createCommonStuff(tome, way1.desc() + "\n\n" + way2.desc() + "\n\n" + TXT_MASTERY)
        val btnWay1 = object : RedButton(Utils.capitalize(way1.title()!!)) {
            override fun onClick() {
                hide()
                tome.choose(way1)
            }
        }
        btnWay1.setRect(0f, bottom + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT.toFloat())
        add(btnWay1)
        val btnWay2 = object : RedButton(Utils.capitalize(way2.title()!!)) {
            override fun onClick() {
                hide()
                tome.choose(way2)
            }
        }
        btnWay2.setRect(btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(), BTN_HEIGHT.toFloat())
        add(btnWay2)
        val btnCancel = object : RedButton(TXT_CANCEL) {
            override fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0f, btnWay2.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom().toInt())
    }
    constructor(tome: TomeOfMastery, way: HeroSubClass) : super() {
        val TXT_REMASTERY = "Do you want to respec into %s?"
        val TXT_OK = "Yes, I want to respec"
        val TXT_CANCEL = "Maybe later"
        val bottom = createCommonStuff(
            tome,
            way.desc() + "\n\n" + Utils.format(TXT_REMASTERY, Utils.indefinite(way.title()!!))
        )
        val btnWay = object : RedButton(TXT_OK) {
            override fun onClick() {
                hide()
                tome.choose(way)
            }
        }
        btnWay.setRect(0f, bottom + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnWay)
        val btnCancel = object : RedButton(TXT_CANCEL) {
            override fun onClick() {
                hide()
            }
        }
        btnCancel.setRect(0f, btnWay.bottom() + GAP, WIDTH.toFloat(), BTN_HEIGHT.toFloat())
        add(btnCancel)
        resize(WIDTH, btnCancel.bottom().toInt())
    }
    private fun createCommonStuff(tome: TomeOfMastery, text: String): Float {
        val titlebar = IconTitle()
        titlebar.icon(ItemSprite(tome.image(), null))
        titlebar.label(tome.name())
        titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
        add(titlebar)
        val hl = HighlightedText(6f)
        hl.text(text, WIDTH)
        hl.setPos(titlebar.left(), titlebar.bottom() + GAP)
        add(hl)
        return hl.bottom()
    }
    companion object {
        private const val WIDTH = 120
        private const val BTN_HEIGHT = 18
        private const val GAP = 2f
    }
}
