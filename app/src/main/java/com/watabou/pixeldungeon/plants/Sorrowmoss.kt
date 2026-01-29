package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PoisonParticle
import com.watabou.pixeldungeon.items.potions.PotionOfToxicGas
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Sorrowmoss : Plant() {
    init {
        image = 2
        plantName = "Sorrowmoss"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buffs.affect(ch, Poison::class.java)!!.set(Poison.durationFactor(ch) * (4 + Dungeon.depth / 2))
        }
        if (Dungeon.visible[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 3)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Sorrowmoss"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_SORROWMOSS
            plantClass = Sorrowmoss::class.java
            alchemyClass = PotionOfToxicGas::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "A Sorrowmoss is a flower (not a moss) with razor-sharp petals, coated with a deadly venom."
    }
}
