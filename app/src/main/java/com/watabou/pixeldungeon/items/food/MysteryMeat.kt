package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.actors.buffs.Slow
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
class MysteryMeat : Food() {
    init {
        name = "mystery meat"
        image = ItemSpriteSheet.MEAT
        energy = Hunger.STARVING - Hunger.HUNGRY
        message = "That food tasted... strange."
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            when (Random.Int(5)) {
                0 -> {
                    GLog.w("Oh it's hot!")
                    Buffs.affect(hero, Burning::class.java)?.reignite(hero)
                }
                1 -> {
                    GLog.w("You can't feel your legs!")
                    Buffs.prolong(hero, Roots::class.java, Paralysis.duration(hero))
                }
                2 -> {
                    GLog.w("You are not feeling well.")
                    Buffs.affect(hero, Poison::class.java)?.set(Poison.durationFactor(hero) * hero.HT / 5)
                }
                3 -> {
                    GLog.w("You are stuffed.")
                    Buffs.prolong(hero, Slow::class.java, Slow.duration(hero))
                }
            }
        }
    }
    override fun info(): String {
        return "Eat at your own risk!"
    }
    override fun price(): Int {
        return 5 * quantity
    }
}
