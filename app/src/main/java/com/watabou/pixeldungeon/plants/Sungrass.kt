package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.effects.particles.ShaftParticle
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class Sungrass : Plant() {
    init {
        image = 4
        plantName = "Sungrass"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buffs.affect(ch, Health::class.java)
        }
        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(ShaftParticle.FACTORY, 0.2f, 3)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Sungrass"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_SUNGRASS
            plantClass = Sungrass::class.java
            alchemyClass = PotionOfHealing::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    class Health : Buff() {
        private var pos: Int = 0
        override fun attachTo(target: Char): Boolean {
            pos = target.pos
            return super.attachTo(target)
        }
        override fun act(): Boolean {
            if (target!!.pos != pos || target!!.HP >= target!!.HT) {
                detach()
            } else {
                target!!.HP = Math.min(target!!.HT, target!!.HP + target!!.HT / 10)
                target!!.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
            }
            spend(STEP)
            return true
        }
        override fun icon(): Int {
            return BuffIndicator.HEALING
        }
        override fun toString(): String {
            return "Herbal healing"
        }
        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(POS, pos)
        }
        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            pos = bundle.getInt(POS)
        }
        companion object {
            private const val STEP = 5f
            private const val POS = "pos"
        }
    }
    companion object {
        private const val TXT_DESC = "Sungrass is renowned for its sap's healing properties."
    }
}
