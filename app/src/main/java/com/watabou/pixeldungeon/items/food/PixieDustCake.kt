package com.watabou.pixeldungeon.items.food
import com.watabou.pixeldungeon.actors.buffs.Barkskin
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import kotlin.math.min
class PixieDustCake : Food() {
    init {
        name = "pixie dust cake"
        image = ItemSpriteSheet.PIXIE_DUST_CAKE
        energy = 200f
        message = "The cake sparkles as it melts on your tongue!"
    }
    override fun execute(hero: Hero, action: String) {
        super.execute(hero, action)
        if (action == AC_EAT) {
            when (Random.Int(3)) {
                0 -> {
                    GLog.p("You see your hands turn invisible!")
                    Buffs.affect(hero, Invisibility::class.java, Invisibility.DURATION)
                }
                1 -> {
                    GLog.p("You feel your skin harden!")
                    Buffs.affect(hero, Barkskin::class.java)?.level(hero.HT / 4)
                }
                2 -> {
                    GLog.p("You feel much better!")
                    if (hero.HP < hero.HT) {
                        hero.HP = min(hero.HP + hero.HT / 4, hero.HT)
                        hero.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                    }
                }
            }
        }
    }
    override fun info(): String {
        return "A small cake dusted with shimmering pixie dust. " +
                "Eating it triggers a random magical effect."
    }
    override fun price(): Int {
        return 20 * quantity
    }
}
