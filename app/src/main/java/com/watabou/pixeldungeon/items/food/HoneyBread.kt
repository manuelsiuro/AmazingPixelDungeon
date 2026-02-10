package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import kotlin.math.min
class HoneyBread : Food() {
    init {
        name = "honey bread"
        image = ItemSpriteSheet.HONEY_BREAD
        energy = 300f
        message = "Sweet and nourishing!"
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            if (hero.HP < hero.HT) {
                hero.HP = min(hero.HP + 5, hero.HT)
                hero.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                GLog.p("The honey soothes your wounds.")
            }
        }
    }
    override fun info(): String {
        return "Golden bread drizzled with wild honey. Its sweetness restores both body and spirit."
    }
    override fun price(): Int {
        return 18 * quantity
    }
}
