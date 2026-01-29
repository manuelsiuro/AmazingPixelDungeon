package com.watabou.pixeldungeon.windows
import com.watabou.gltextures.SmartTexture
import com.watabou.gltextures.TextureCache
import com.watabou.noosa.BitmapText
import com.watabou.noosa.Group
import com.watabou.noosa.Image
import com.watabou.noosa.TextureFilm
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.utils.Utils
import java.util.Locale
class WndHero : WndTabbed() {
    private val stats: StatsTab
    private val buffs: BuffsTab
    private val icons: SmartTexture
    private val film: TextureFilm
    init {
        icons = TextureCache.get(Assets.BUFFS_LARGE)
        film = TextureFilm(icons, 16, 16)
        stats = StatsTab()
        add(stats)
        buffs = BuffsTab()
        add(buffs)
        add(object : LabeledTab(TXT_STATS) {
            override fun select(value: Boolean) {
                super.select(value)
                stats.visible = selected
                stats.active = selected
            }
        })
        add(object : LabeledTab(TXT_BUFFS) {
            override fun select(value: Boolean) {
                super.select(value)
                buffs.visible = selected
                buffs.active = selected
            }
        })
        for (tab in tabs) {
            tab.setSize(TAB_WIDTH.toFloat(), tabHeight().toFloat())
        }
        resize(WIDTH, Math.max(stats.currentHeight(), buffs.currentHeight()).toInt())
        select(0)
    }
    private inner class StatsTab : Group() {
        var pos: Float = 0.toFloat()
        init {
            val hero = Dungeon.hero!!
            val title = PixelScene.createText(
                Utils.format(TXT_TITLE, hero.lvl, hero.className()).uppercase(Locale.ENGLISH), 9f
            )
            title.hardlight(TITLE_COLOR)
            title.measure()
            add(title)
            val btnCatalogus = object : RedButton(TXT_CATALOGUS) {
                override fun onClick() {
                    hide()
                    GameScene.show(WndCatalogus())
                }
            }
            btnCatalogus.setRect(
                0f,
                title.y + title.height(),
                btnCatalogus.reqWidth() + 2,
                btnCatalogus.reqHeight() + 2
            )
            add(btnCatalogus)
            val btnJournal = object : RedButton(TXT_JOURNAL) {
                override fun onClick() {
                    hide()
                    GameScene.show(WndJournal())
                }
            }
            btnJournal.setRect(
                btnCatalogus.right() + 1,
                btnCatalogus.top(),
                btnJournal.reqWidth() + 2,
                btnJournal.reqHeight() + 2
            )
            add(btnJournal)
            pos = btnCatalogus.bottom() + GAP
            statSlot(TXT_STR, hero.STR())
            statSlot(TXT_HEALTH, "${hero.HP}/${hero.HT}")
            statSlot(TXT_EXP, "${hero.exp}/${hero.maxExp()}")
            pos += GAP
            statSlot(TXT_GOLD, Statistics.goldCollected)
            statSlot(TXT_DEPTH, Statistics.deepestFloor)
            pos += GAP
        }
        private fun statSlot(label: String, value: String) {
            var txt = PixelScene.createText(label, 8f)
            txt.y = pos
            add(txt)
            txt = PixelScene.createText(value, 8f)
            txt.measure()
            txt.x = PixelScene.align(WIDTH * 0.65f)
            txt.y = pos
            add(txt)
            pos += GAP + txt.baseLine()
        }
        private fun statSlot(label: String, value: Int) {
            statSlot(label, Integer.toString(value))
        }
        fun currentHeight(): Float { // Renamed from height() to avoid conflict with Gizmo.height
            return pos
        }
    }
    private inner class BuffsTab : Group() {
        var pos: Float = 0.toFloat()
        init {
            for (buff in Dungeon.hero!!.buffs()) {
                buffSlot(buff)
            }
        }
        private fun buffSlot(buff: Buff) {
            val index = buff.icon()
            if (index != BuffIndicator.NONE) {
                val icon = Image(icons)
                icon.frame(film[index]!!)
                icon.y = pos
                add(icon)
                val txt = PixelScene.createText(buff.toString(), 8f)
                txt.x = icon.width + GAP
                txt.y = pos + (icon.height - txt.baseLine()) / 2
                add(txt)
                pos += GAP + icon.height
            }
        }
        fun currentHeight(): Float { // Renamed from height() to avoid conflict with Gizmo.height
            return pos
        }
    }
    companion object {
        private const val TXT_STATS = "Stats"
        private const val TXT_BUFFS = "Buffs"
        private const val TXT_EXP = "Experience"
        private const val TXT_STR = "Strength"
        private const val TXT_HEALTH = "Health"
        private const val TXT_GOLD = "Gold Collected"
        private const val TXT_DEPTH = "Maximum Depth"
        private const val TXT_TITLE = "Level %d %s"
        private const val TXT_CATALOGUS = "Catalogus"
        private const val TXT_JOURNAL = "Journal"
        private const val WIDTH = 100
        private const val TAB_WIDTH = 40
        private const val GAP = 5
    }
}
