package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class FrostBerry : Food() {
    init {
        name = "frost berry"
        image = ItemSpriteSheet.FROST_BERRY
        energy = 80f
        message = "A cooling sensation washes over you."
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            Buffs.detach(hero, Burning::class.java)
            Buffs.detach(hero, Weakness::class.java)
            GLog.p("You feel refreshed and clear-headed.")
        }
    }
    override fun info(): String {
        return "A small blue berry coated in frost. It removes burning and weakness when eaten."
    }
    override fun price(): Int {
        return 10 * quantity
    }
}
