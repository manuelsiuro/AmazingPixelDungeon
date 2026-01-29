package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.utils.GLog
class PotionOfHealing : Potion() {
    init {
        name = "Potion of Healing"
    }
    override fun apply(hero: Hero) {
        setKnown()
        val currentHero = Dungeon.hero ?: return
        heal(currentHero)
        GLog.p("Your wounds heal completely.")
    }
    override fun desc(): String {
        return "An elixir that will instantly return you to full health and cure poison."
    }
    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
    companion object {
        fun heal(hero: Hero) {
            hero.HP = hero.HT
            Buffs.detach(hero, Poison::class.java)
            Buffs.detach(hero, Cripple::class.java)
            Buffs.detach(hero, Weakness::class.java)
            Buffs.detach(hero, Bleeding::class.java)
            hero.sprite?.emitter()?.start(Speck.factory(Speck.HEALING), 0.4f, 4)
        }
    }
}
