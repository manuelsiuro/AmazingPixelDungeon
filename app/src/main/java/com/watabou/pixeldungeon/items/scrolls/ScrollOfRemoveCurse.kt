package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfRemoveCurse : Scroll() {
    init {
        name = "Scroll of Remove Curse"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val sprite = hero.sprite ?: return
        Flare(6, 32f).show(sprite, 2f)
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        var procced = uncurse(hero, *hero.belongings.backpack.items.toTypedArray())
        procced = uncurse(hero,
            hero.belongings.weapon,
            hero.belongings.armor,
            hero.belongings.ring1,
            hero.belongings.ring2) || procced
        Buffs.detach(hero, Weakness::class.java)
        if (procced) {
            GLog.p(TXT_PROCCED)
        } else {
            GLog.i(TXT_NOT_PROCCED)
        }
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "The incantation on this scroll will instantly strip from " +
                "the reader's weapon, armor, rings and carried items any evil " +
                "enchantments that might prevent the wearer from removing them."
    }
    override fun price(): Int {
        return if (isKnown) 30 * quantity else super.price()
    }
    companion object {
        private const val TXT_PROCCED = "Your pack glows with a cleansing light, and a malevolent energy disperses."
        private const val TXT_NOT_PROCCED = "Your pack glows with a cleansing light, but nothing happens."
        fun uncurse(hero: Hero, vararg items: Item?): Boolean {
            var procced = false
            for (item in items) {
                if (item != null && item.cursed) {
                    item.cursed = false
                    procced = true
                }
            }
            if (procced) {
                hero.sprite?.emitter()?.start(ShadowParticle.UP, 0.05f, 10)
            }
            return procced
        }
    }
}
