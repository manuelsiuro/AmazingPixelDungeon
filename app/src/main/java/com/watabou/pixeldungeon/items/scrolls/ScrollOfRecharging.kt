package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.SpellSprite
import com.watabou.pixeldungeon.effects.particles.EnergyParticle
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfRecharging : Scroll() {
    init {
        name = "Scroll of Recharging"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val count = hero.belongings.charge(true)
        charge(hero)
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        if (count > 0) {
            GLog.i("a surge of energy courses through your pack, recharging your wand" + (if (count > 1) "s" else ""))
            SpellSprite.show(hero, SpellSprite.CHARGE)
        } else {
            GLog.i("a surge of energy courses through your pack, but nothing happens")
        }
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "The raw magical power bound up in this parchment will, when released, " +
                "recharge all of the reader's wands to full power."
    }
    override fun price(): Int {
        return if (isKnown) 40 * quantity else super.price()
    }
    companion object {
        fun charge(hero: Hero) {
            hero.sprite?.centerEmitter()?.burst(EnergyParticle.FACTORY, 15)
        }
    }
}
