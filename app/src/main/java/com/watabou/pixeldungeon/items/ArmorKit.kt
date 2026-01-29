package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.armor.ClassArmor
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.HeroSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
import java.util.ArrayList
class ArmorKit : Item() {
    init {
        name = "armor kit"
        image = ItemSpriteSheet.KIT
        unique = true
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_APPLY)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_APPLY) {
            curUser = hero
            GameScene.selectItem(itemSelector, WndBag.Mode.ARMOR, TXT_SELECT_ARMOR)
        } else {
            super.execute(hero, action)
        }
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    private fun upgrade(armor: Armor) {
        val user = curUser ?: return
        detach(user.belongings.backpack)
        user.sprite?.centerEmitter()?.start(Speck.factory(Speck.KIT), 0.05f, 10)
        user.spend(TIME_TO_UPGRADE)
        user.busy()
        GLog.w(TXT_UPGRADED, armor.name())
        val classArmor = ClassArmor.upgrade(user, armor)
        if (user.belongings.armor === armor) {
            user.belongings.armor = classArmor
            (user.sprite as? HeroSprite)?.updateArmor()
        } else {
            armor.detach(user.belongings.backpack)
            classArmor.collect(user.belongings.backpack)
        }
        user.sprite?.operate(user.pos)
        Sample.play(Assets.SND_EVOKE)
    }
    override fun info(): String {
        return "Using this kit of small tools and materials anybody can transform any armor into an \"epic armor\", " +
                "which will keep all properties of the original armor, but will also provide its wearer a special ability " +
                "depending on his class. No skills in tailoring, leatherworking or blacksmithing are required."
    }
    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                upgrade(item as Armor)
            }
        }
    }
    companion object {
        private const val TXT_SELECT_ARMOR = "Select an armor to upgrade"
        private const val TXT_UPGRADED = "you applied the armor kit to upgrade your %s"
        private const val TIME_TO_UPGRADE = 2f
        private const val AC_APPLY = "APPLY"
    }
}
