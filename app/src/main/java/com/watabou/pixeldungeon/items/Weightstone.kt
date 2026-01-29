package com.watabou.pixeldungeon.items
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.pixeldungeon.windows.IconTitle
import com.watabou.pixeldungeon.windows.WndBag
import java.util.ArrayList
class Weightstone : Item() {
    init {
        name = "weightstone"
        image = ItemSpriteSheet.WEIGHT
        stackable = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_APPLY) {
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON)
        } else {
            super.execute(hero, action)
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    private fun apply(weapon: Weapon, forSpeed: Boolean) {
        val user = curUser ?: return
        detach(user.belongings.backpack)
        weapon.fix()
        if (forSpeed) {
            weapon.imbue = Weapon.Imbue.SPEED
            GLog.p(TXT_FAST, weapon.name())
        } else {
            weapon.imbue = Weapon.Imbue.ACCURACY
            GLog.p(TXT_ACCURATE, weapon.name())
        }
        user.sprite?.operate(user.pos)
        Sample.play(Assets.SND_MISS)
        user.spend(TIME_TO_APPLY)
        user.busy()
    }
    override fun price(): Int {
        return 40 * quantity
    }
    override fun info(): String {
        return "Using a weightstone, you can balance your melee weapon to increase its speed or accuracy."
    }
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                GameScene.show(WndBalance(item as Weapon))
            }
        }
    }
    inner class WndBalance(private val weapon: Weapon) : Window() {
        init {
            val titlebar = IconTitle(weapon)
            titlebar.setRect(0f, 0f, WIDTH.toFloat(), 0f)
            add(titlebar)
            val tfMesage = PixelScene.createMultiline(Utils.format(TXT_CHOICE, weapon.name()), 8f)
            tfMesage.maxWidth = WIDTH - MARGIN * 2
            tfMesage.measure()
            tfMesage.x = MARGIN.toFloat()
            tfMesage.y = titlebar.bottom() + MARGIN
            add(tfMesage)
            var pos = tfMesage.y + tfMesage.height()
            if (weapon.imbue !== Weapon.Imbue.SPEED) {
                val btnSpeed = object : RedButton(TXT_SPEED) {
                    override fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, true)
                    }
                }
                btnSpeed.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
                add(btnSpeed)
                pos = btnSpeed.bottom()
            }
            if (weapon.imbue !== Weapon.Imbue.ACCURACY) {
                val btnAccuracy = object : RedButton(TXT_ACCURACY) {
                    override fun onClick() {
                        hide()
                        this@Weightstone.apply(weapon, false)
                    }
                }
                btnAccuracy.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
                add(btnAccuracy)
                pos = btnAccuracy.bottom()
            }
            val btnCancel = object : RedButton(TXT_CANCEL) {
                override fun onClick() {
                    hide()
                }
            }
            btnCancel.setRect(MARGIN.toFloat(), pos + MARGIN, BUTTON_WIDTH.toFloat(), BUTTON_HEIGHT.toFloat())
            add(btnCancel)
            resize(WIDTH, (btnCancel.bottom() + MARGIN).toInt())
        }
    }
    companion object {
        private const val TXT_SELECT_WEAPON = "Select a weapon to balance"
        private const val TXT_FAST = "you balanced your %s to make it faster"
        private const val TXT_ACCURATE = "you balanced your %s to make it more accurate"
        private const val TIME_TO_APPLY = 2f
        private const val AC_APPLY = "APPLY"
        private const val TXT_CHOICE = "How would you like to balance your %s?"
        private const val TXT_SPEED = "For speed"
        private const val TXT_ACCURACY = "For accuracy"
        private const val TXT_CANCEL = "Never mind"
        private const val WIDTH = 120
        private const val MARGIN = 2
        private const val BUTTON_WIDTH = WIDTH - MARGIN * 2
        private const val BUTTON_HEIGHT = 20
    }
}
