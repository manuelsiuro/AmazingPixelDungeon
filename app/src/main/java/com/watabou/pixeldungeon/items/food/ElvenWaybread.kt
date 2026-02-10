package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
class ElvenWaybread : Food() {
    init {
        name = "elven waybread"
        image = ItemSpriteSheet.ELVEN_WAYBREAD
        energy = 360f
        message = "You feel completely restored!"
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            Buffs.detach(hero, Poison::class.java)
            Buffs.detach(hero, Cripple::class.java)
            Buffs.detach(hero, Weakness::class.java)
            Buffs.detach(hero, Bleeding::class.java)
            GLog.p("A warm light fills you as all ailments fade away.")
        }
    }
    override fun info(): String {
        return "A leaf-wrapped loaf of elvish bread, baked with ancient recipes. " +
                "It fills the stomach completely and cleanses all ailments."
    }
    override fun price(): Int {
        return 30 * quantity
    }
}
