package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.actors.hero.Hero
class PotionOfExperience : Potion() {
    init {
        name = "Potion of Experience"
    }
    override fun apply(hero: Hero) {
        setKnown()
        hero.earnExp(hero.maxExp() - hero.exp)
    }
    override fun desc(): String {
        return "The storied experiences of multitudes of battles reduced to liquid form, " +
                "this draught will instantly raise your experience level."
    }
    override fun price(): Int {
        return if (isKnown) 80 * quantity else super.price()
    }
}
