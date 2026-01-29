package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.food.FrozenCarpaccio
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.ui.BuffIndicator
class Frost : FlavourBuff() {
    override fun attachTo(target: Char): Boolean {
        return if (super.attachTo(target)) {
            target.paralysed = true
            Buffs.detach(target, Burning::class.java)
            if (target is Hero) {
                val item = target.belongings.randomUnequipped()
                if (item is MysteryMeat) {
                    item.detach(target.belongings.backpack)
                    val carpaccio = FrozenCarpaccio()
                    if (!carpaccio.collect(target.belongings.backpack)) {
                        Dungeon.level?.drop(carpaccio, target.pos)?.sprite?.drop()
                    }
                }
            }
            true
        } else {
            false
        }
    }
    override fun detach() {
        super.detach()
        target?.let { Paralysis.unfreeze(it) }
    }
    override fun icon(): Int {
        return BuffIndicator.FROST
    }
    override fun toString(): String {
        return "Frozen"
    }
    companion object {
        private const val DURATION = 5f
        fun duration(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}
