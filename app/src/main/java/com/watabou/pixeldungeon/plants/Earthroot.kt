package com.watabou.pixeldungeon.plants
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.EarthParticle
import com.watabou.pixeldungeon.items.potions.PotionOfParalyticGas
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Bundle
class Earthroot : Plant() {
    init {
        image = 5
        plantName = "Earthroot"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buffs.affect(ch, Armor::class.java)!!.level = ch.HT
        }
        if (Dungeon.visible[pos]) {
            CellEmitter.bottom(pos).start(EarthParticle.FACTORY, 0.05f, 8)
            Camera.main?.shake(1f, 0.4f)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Earthroot"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_EARTHROOT
            plantClass = Earthroot::class.java
            alchemyClass = PotionOfParalyticGas::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    open class Armor : Buff() {
        private var pos: Int = 0
        var level: Int = 0
        override fun attachTo(target: Char): Boolean {
            pos = target.pos
            return super.attachTo(target)
        }
        override fun act(): Boolean {
            if (target!!.pos != pos) {
                detach()
            }
            spend(STEP)
            return true
        }
        fun absorb(damage: Int): Int {
            if (damage >= level) {
                detach()
                return damage - level
            } else {
                level -= damage
                return 0
            }
        }
        fun level(value: Int) {
            if (level < value) {
                level = value
            }
        }
        override fun icon(): Int {
            return BuffIndicator.ARMOR
        }
        override fun toString(): String {
            return "Herbal armor"
        }
        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(POS, pos)
            bundle.put(LEVEL, level)
        }
        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            pos = bundle.getInt(POS)
            level = bundle.getInt(LEVEL)
        }
        companion object {
            private const val STEP = 1f
            private const val POS = "pos"
            private const val LEVEL = "level"
        }
    }
    companion object {
        private const val TXT_DESC = "When a creature touches an Earthroot, its roots create a kind of natural armor around it."
    }
}
