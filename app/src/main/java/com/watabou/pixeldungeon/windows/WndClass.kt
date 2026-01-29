package com.watabou.pixeldungeon.windows
import com.watabou.noosa.BitmapText
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.Group
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.HighlightedText
import com.watabou.pixeldungeon.utils.Utils
class WndClass(private val cl: HeroClass) : WndTabbed() {
    private val tabPerks: PerksTab
    private var tabMastery: MasteryTab? = null
    init {
        tabPerks = PerksTab()
        add(tabPerks)
        add(object : LabeledTab(Utils.capitalize(cl.title())) {
            override fun select(value: Boolean): Unit {
                super.select(value)
                tabPerks.visible = selected
                tabPerks.active = selected
            }
        })
        val badge = cl.masteryBadge()
        if (badge != null && Badges.isUnlocked(badge)) {
            tabMastery = MasteryTab()
            add(tabMastery!!)
            add(object : LabeledTab(TXT_MASTERY) {
                override fun select(value: Boolean): Unit {
                    super.select(value)
                    val tm = tabMastery
                    if (tm != null) {
                        tm.visible = selected
                        tm.active = selected
                    }
                }
            })
            resize(
                Math.max(tabPerks.tabWidth, tabMastery!!.tabWidth).toInt(),
                Math.max(tabPerks.tabHeight, tabMastery!!.tabHeight).toInt()
            )
        } else {
            resize(tabPerks.tabWidth.toInt(), tabPerks.tabHeight.toInt())
        }
        for (tab in tabs) {
             tab.setSize(TAB_WIDTH.toFloat(), tabHeight().toFloat())
        }
        select(0)
    }
    private inner class PerksTab : Group() {
        var tabHeight: Float = 0.toFloat()
        var tabWidth: Float = 0.toFloat()
        init {
            var dotWidth = 0f
            var pos = MARGIN.toFloat()
            val items = cl.perks()
            if (items != null) {
                for (i in items.indices) {
                    if (i > 0) {
                        pos += GAP
                    }
                    val dot = PixelScene.createText(DOT, 6f)
                    dot.x = MARGIN.toFloat()
                    dot.y = pos
                    if (dotWidth == 0f) {
                        dot.measure()
                        dotWidth = dot.width()
                    }
                    add(dot)
                    val item = PixelScene.createMultiline(items[i], 6f)
                    item.x = dot.x + dotWidth
                    item.y = pos
                    item.maxWidth = (WIDTH - MARGIN * 2 - dotWidth).toInt()
                    item.measure()
                    add(item)
                    pos += item.height()
                    val w = item.width()
                    if (w > tabWidth) {
                        tabWidth = w
                    }
                }
            }
            tabWidth += MARGIN + dotWidth
            tabHeight = pos + MARGIN
        }
    }
    private inner class MasteryTab : Group() {
        var tabHeight: Float = 0.toFloat()
        var tabWidth: Float = 0.toFloat()
        init {
            var message: String? = null
            when (cl) {
                HeroClass.WARRIOR -> message = HeroSubClass.GLADIATOR.desc() + "\n\n" + HeroSubClass.BERSERKER.desc()
                HeroClass.MAGE -> message = HeroSubClass.BATTLEMAGE.desc() + "\n\n" + HeroSubClass.WARLOCK.desc()
                HeroClass.ROGUE -> message = HeroSubClass.FREERUNNER.desc() + "\n\n" + HeroSubClass.ASSASSIN.desc()
                HeroClass.HUNTRESS -> message = HeroSubClass.SNIPER.desc() + "\n\n" + HeroSubClass.WARDEN.desc()
            }
            val text = HighlightedText(6f)
            text.text(message, WIDTH - MARGIN * 2)
            text.setPos(MARGIN.toFloat(), MARGIN.toFloat())
            add(text)
            tabHeight = text.bottom() + MARGIN
            tabWidth = text.right() + MARGIN
        }
    }
    companion object {
        private const val TXT_MASTERY = "Mastery"
        private const val WIDTH = 110
        private const val TAB_WIDTH = 50
        // Moved constants from inner classes
        private const val MARGIN = 4
        private const val GAP = 4
        private const val DOT = "\u007F"
    }
}
