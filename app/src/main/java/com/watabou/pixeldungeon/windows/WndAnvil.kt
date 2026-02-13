package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapText
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.crafting.AnvilManager
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.EnchantedBook
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog

class WndAnvil(private val hero: Hero) : Window() {

    private lateinit var repairItem1: WndBlacksmith.ItemButton
    private lateinit var repairItem2: WndBlacksmith.ItemButton
    private lateinit var repairBtn: RedButton

    private lateinit var applyWeapon: WndBlacksmith.ItemButton
    private lateinit var applyBook: WndBlacksmith.ItemButton
    private lateinit var applyBtn: RedButton

    private var btnPressed: WndBlacksmith.ItemButton? = null

    private val repairSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                btnPressed?.item(item)
                updateRepairButton()
            }
        }
    }

    private val weaponSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                applyWeapon.item(item)
                updateApplyButton()
            }
        }
    }

    private val bookSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null && item is EnchantedBook) {
                applyBook.item(item)
                updateApplyButton()
            }
        }
    }

    init {
        val title = PixelScene.createText("Anvil", 9f)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = (WIDTH - title.width()) / 2
        title.y = 0f
        add(title)

        var pos = title.height() + GAP * 2

        // === Repair Section ===
        val repairLabel = PixelScene.createText("Repair", 7f)
        repairLabel.hardlight(TITLE_COLOR)
        repairLabel.measure()
        repairLabel.x = 0f
        repairLabel.y = pos
        add(repairLabel)

        pos = repairLabel.y + repairLabel.height() + GAP

        repairItem1 = object : WndBlacksmith.ItemButton() {
            override fun onClick() {
                btnPressed = repairItem1
                GameScene.selectItem(repairSelector, WndBag.Mode.WEAPON, TXT_SELECT_REPAIR)
            }
        }
        repairItem1.setRect(
            (WIDTH - BTN_GAP) / 2 - BTN_SIZE,
            pos,
            BTN_SIZE.toFloat(),
            BTN_SIZE.toFloat()
        )
        add(repairItem1)

        repairItem2 = object : WndBlacksmith.ItemButton() {
            override fun onClick() {
                btnPressed = repairItem2
                GameScene.selectItem(repairSelector, WndBag.Mode.WEAPON, TXT_SELECT_REPAIR)
            }
        }
        repairItem2.setRect(
            repairItem1.right() + BTN_GAP,
            pos,
            BTN_SIZE.toFloat(),
            BTN_SIZE.toFloat()
        )
        add(repairItem2)

        pos = repairItem1.bottom() + GAP

        repairBtn = object : RedButton(TXT_REPAIR) {
            override fun onClick() {
                doRepair()
            }
        }
        repairBtn.enable(false)
        repairBtn.setRect(0f, pos, WIDTH.toFloat(), 18f)
        add(repairBtn)

        pos = repairBtn.bottom() + GAP * 3

        // === Apply Enchantment Section ===
        val applyLabel = PixelScene.createText("Apply Enchantment", 7f)
        applyLabel.hardlight(TITLE_COLOR)
        applyLabel.measure()
        applyLabel.x = 0f
        applyLabel.y = pos
        add(applyLabel)

        pos = applyLabel.y + applyLabel.height() + GAP

        applyWeapon = object : WndBlacksmith.ItemButton() {
            override fun onClick() {
                GameScene.selectItem(weaponSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON)
            }
        }
        applyWeapon.setRect(
            (WIDTH - BTN_GAP) / 2 - BTN_SIZE,
            pos,
            BTN_SIZE.toFloat(),
            BTN_SIZE.toFloat()
        )
        add(applyWeapon)

        applyBook = object : WndBlacksmith.ItemButton() {
            override fun onClick() {
                GameScene.selectItem(bookSelector, WndBag.Mode.BOOK, TXT_SELECT_BOOK)
            }
        }
        applyBook.setRect(
            applyWeapon.right() + BTN_GAP,
            pos,
            BTN_SIZE.toFloat(),
            BTN_SIZE.toFloat()
        )
        add(applyBook)

        pos = applyWeapon.bottom() + GAP

        applyBtn = object : RedButton(TXT_APPLY) {
            override fun onClick() {
                doApply()
            }
        }
        applyBtn.enable(false)
        applyBtn.setRect(0f, pos, WIDTH.toFloat(), 18f)
        add(applyBtn)

        resize(WIDTH, (applyBtn.bottom() + GAP).toInt())
    }

    private fun updateRepairButton() {
        val i1 = repairItem1.item
        val i2 = repairItem2.item
        repairBtn.enable(i1 != null && i2 != null && AnvilManager.canRepair(i1, i2))
    }

    private fun updateApplyButton() {
        val w = applyWeapon.item
        val b = applyBook.item
        applyBtn.enable(w != null && b != null && AnvilManager.canApplyBook(w, b))
    }

    private fun doRepair() {
        val i1 = repairItem1.item ?: return
        val i2 = repairItem2.item ?: return
        val result = AnvilManager.repair(hero, i1, i2)
        if (result != null) {
            GLog.p("Your %s has been repaired!", result.name())
            Sample.play(Assets.SND_EVOKE)
        }
        hide()
    }

    private fun doApply() {
        val w = applyWeapon.item ?: return
        val b = applyBook.item ?: return
        if (AnvilManager.applyBook(hero, w, b)) {
            GLog.p("The enchantment flows into your %s!", w.name())
            Sample.play(Assets.SND_EVOKE)
        } else {
            GLog.w("You can't afford this enchantment.")
        }
        hide()
    }

    companion object {
        private const val WIDTH = 120
        private const val GAP = 2f
        private const val BTN_GAP = 10f
        private const val BTN_SIZE = 36f
        private const val TXT_SELECT_REPAIR = "Select a weapon to repair"
        private const val TXT_SELECT_WEAPON = "Select a weapon"
        private const val TXT_SELECT_BOOK = "Select an enchanted book"
        private const val TXT_REPAIR = "Repair"
        private const val TXT_APPLY = "Apply Enchantment"
    }
}
