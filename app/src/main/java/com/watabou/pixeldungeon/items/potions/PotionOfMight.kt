package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
class PotionOfMight : PotionOfStrength() {
    init {
        name = "Potion of Might"
    }
    override fun apply(hero: Hero) {
        setKnown()
        hero.STR++
        hero.HT += 5
        hero.HP += 5
        hero.sprite?.showStatus(CharSprite.POSITIVE, "+1 str, +5 ht")
        GLog.p("Newfound strength surges through your body.")
        Badges.validateStrengthAttained()
    }
    override fun desc(): String {
        return "This powerful liquid will course through your muscles, permanently " +
                "increasing your strength by one point and health by five points."
    }
    override fun price(): Int {
        return if (isKnown) 200 * quantity else super.price()
    }
}
