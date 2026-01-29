package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.items.weapon.missiles.Boomerang
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
import java.util.ArrayList
class ShortSword : MeleeWeapon(1, 1f, 1f) {
    private var equipped: Boolean = false
    init {
        name = "short sword"
        image = ItemSpriteSheet.SHORT_SWORD
        STR = 11
    }
    override fun max0(): Int {
        return 12
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (level() > 0) {
            actions.add(AC_REFORGE)
        }
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_REFORGE) {
            if (hero.belongings.weapon === this) {
                equipped = true
                hero.belongings.weapon = null
            } else {
                equipped = false
                detach(hero.belongings.backpack)
            }
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.WEAPON, TXT_SELECT_WEAPON)
        } else {
            super.execute(hero, action)
        }
    }
    override fun desc(): String {
        return "It is indeed quite short, just a few inches longer, than a dagger."
    }
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            val user = curUser
            if (item != null && item !is Boomerang && user is Hero) {
                Sample.play(Assets.SND_EVOKE)
                ScrollOfUpgrade.upgrade(user)
                evoke(user)
                GLog.w(TXT_REFORGED, item.name())
                if (item is MeleeWeapon) {
                    item.safeUpgrade()
                } else {
                    (item as MeleeWeapon).safeUpgrade()
                }
                user.spendAndNext(TIME_TO_REFORGE)
                Badges.validateItemLevelAquired(item)
            } else {
                if (item is Boomerang) {
                    GLog.w(TXT_NOT_BOOMERANG)
                }
                if (user != null) {
                    if (equipped) {
                        user.belongings.weapon = this@ShortSword
                    } else {
                        collect(user.belongings.backpack)
                    }
                }
            }
        }
    }
    companion object {
        const val AC_REFORGE = "REFORGE"
        private const val TXT_SELECT_WEAPON = "Select a weapon to upgrade"
        private const val TXT_REFORGED = "you reforged the short sword to upgrade your %s"
        private const val TXT_NOT_BOOMERANG = "you can't upgrade a boomerang this way"
        private const val TIME_TO_REFORGE = 2f
    }
}
