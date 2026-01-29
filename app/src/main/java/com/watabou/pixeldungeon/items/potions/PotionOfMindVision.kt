package com.watabou.pixeldungeon.items.potions
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.MindVision
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.utils.GLog
class PotionOfMindVision : Potion() {
    init {
        name = "Potion of Mind Vision"
    }
    override fun apply(hero: Hero) {
        setKnown()
        Buffs.affect(hero, MindVision::class.java, MindVision.DURATION)
        Dungeon.observe()
        val level = Dungeon.level
        if (level != null && level.mobs.size > 0) {
            GLog.i("You can somehow feel the presence of other creatures' minds!")
        } else {
            GLog.i("You can somehow tell that you are alone on this level at the moment.")
        }
    }
    override fun desc(): String {
        return "After drinking this, your mind will become attuned to the psychic signature " +
                "of distant creatures, enabling you to sense biological presences through walls. " +
                "Also this potion will permit you to see through nearby walls and doors."
    }
    override fun price(): Int {
        return if (isKnown) 35 * quantity else super.price()
    }
}
