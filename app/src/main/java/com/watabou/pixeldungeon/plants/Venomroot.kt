package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.PoisonParticle
import com.watabou.pixeldungeon.items.potions.PotionOfToxicGas
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Venomroot : Plant() {

    init {
        image = 11
        plantName = "Venomroot"
    }

    override fun activate(ch: Char?) {
        super.activate(ch)

        GameScene.add(Blob.seed(pos, 300, ToxicGas::class.java)!!)

        if (Dungeon.visible[pos]) {
            CellEmitter.center(pos).burst(PoisonParticle.SPLASH, 5)
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        init {
            plantName = "Venomroot"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_VENOMROOT
            plantClass = Venomroot::class.java
            alchemyClass = PotionOfToxicGas::class.java
        }

        override fun desc(): String {
            return TXT_DESC
        }
    }

    companion object {
        private const val TXT_DESC =
            "The Venomroot releases a cloud of toxic spores when stepped on, poisoning everything in the area."
    }
}
