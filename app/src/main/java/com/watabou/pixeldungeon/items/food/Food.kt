package com.watabou.pixeldungeon.items.food
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.scrolls.ScrollOfRecharging
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import java.util.ArrayList
import kotlin.math.min
open class Food : Item() {
    var energy: Float = Hunger.HUNGRY
    var message: String = "That food tasted delicious!"
    init {
        stackable = true
        name = "ration of food"
        image = ItemSpriteSheet.RATION
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(AC_EAT)
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_EAT) {
            detach(hero.belongings.backpack)
            val hunger = hero.buff(Hunger::class.java) as Hunger
            hunger.satisfy(energy)
            GLog.i(message)
            when (hero.heroClass) {
                HeroClass.WARRIOR -> if (hero.HP < hero.HT) {
                    hero.HP = min(hero.HP + 5, hero.HT)
                    hero.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                }
                HeroClass.MAGE -> {
                    hero.belongings.charge(false)
                    ScrollOfRecharging.charge(hero)
                }
                HeroClass.ROGUE, HeroClass.HUNTRESS -> {
                }
            }
            hero.sprite?.operate(hero.pos)
            hero.busy()
            SpellSprite.show(hero, SpellSprite.FOOD)
            Sample.play(Assets.SND_EAT)
            hero.spend(TIME_TO_EAT)
            Statistics.foodEaten++
            Badges.validateFoodEaten()
        } else {
            super.execute(hero, action)
        }
    }
    override fun info(): String {
        return "Nothing fancy here: dried meat, " +
                "some biscuits - things like that."
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun price(): Int {
        return 10 * quantity
    }
    companion object {
        const val AC_EAT = "EAT"
        private const val TIME_TO_EAT = 3f
    }
}
