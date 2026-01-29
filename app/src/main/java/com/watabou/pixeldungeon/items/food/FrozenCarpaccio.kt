package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Barkskin
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import kotlin.math.min
class FrozenCarpaccio : Food() {
    init {
        name = "frozen carpaccio"
        image = ItemSpriteSheet.CARPACCIO
        energy = Hunger.STARVING - Hunger.HUNGRY
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.i("You see your hands turn invisible!")
                    Buffs.affect(hero, Invisibility::class.java, Invisibility.DURATION)
                }
                1 -> {
                    GLog.i("You feel your skin hardens!")
                    Buffs.affect(hero, Barkskin::class.java)?.level(hero.HT / 4)
                }
                2 -> {
                    GLog.i("Refreshing!")
                    Buffs.detach(hero, Poison::class.java)
                    Buffs.detach(hero, Cripple::class.java)
                    Buffs.detach(hero, Weakness::class.java)
                    Buffs.detach(hero, Bleeding::class.java)
                }
                3 -> {
                    GLog.i("You feel better!")
                    if (hero.HP < hero.HT) {
                        hero.HP = min(hero.HP + hero.HT / 4, hero.HT)
                        hero.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                    }
                }
            }
        }
    }
    override fun info(): String {
        return "It's a piece of frozen raw meat. The only way to eat it is " +
                "by cutting thin slices of it. And this way it's suprisingly good."
    }
    override fun price(): Int {
        return 10 * quantity
    }
    companion object {
        fun cook(ingredient: MysteryMeat): Food {
            val result = FrozenCarpaccio()
            result.quantity = ingredient.quantity
            return result
        }
    }
}
