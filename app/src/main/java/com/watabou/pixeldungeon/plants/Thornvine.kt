package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PoisonParticle
import com.watabou.pixeldungeon.items.potions.PotionOfParalyticGas
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Thornvine : Plant() {

    init {
        image = 8
        plantName = "Thornvine"
    }

    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch != null) {
            Buffs.affect(ch, Bleeding::class.java)?.set(5)
            Buffs.prolong(ch, Cripple::class.java, 3f)
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
            plantName = "Thornvine"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_THORNVINE
            plantClass = Thornvine::class.java
            alchemyClass = PotionOfParalyticGas::class.java
        }

        override fun desc(): String {
            return TXT_DESC
        }
    }

    companion object {
        private const val TXT_DESC =
            "Thornvine is covered in razor-sharp barbs that tear flesh and hamper movement."
    }
}
