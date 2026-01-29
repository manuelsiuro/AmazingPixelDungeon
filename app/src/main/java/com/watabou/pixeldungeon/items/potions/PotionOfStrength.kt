package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
open class PotionOfStrength : Potion() {
    init {
        name = "Potion of Strength"
    }
    override fun apply(hero: Hero) {
        setKnown()
        hero.STR++
        hero.sprite?.showStatus(CharSprite.POSITIVE, "+1 str")
        GLog.p("Newfound strength surges through your body.")
        Badges.validateStrengthAttained()
    }
    override fun desc(): String {
        return "This powerful liquid will course through your muscles, " +
                "permanently increasing your strength by one point."
    }
    override fun price(): Int {
        return if (isKnown) 100 * quantity else super.price()
    }
}
