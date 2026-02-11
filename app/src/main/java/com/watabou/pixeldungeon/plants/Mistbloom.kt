package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.SmokeParticle
import com.watabou.pixeldungeon.items.potions.PotionOfInvisibility
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Mistbloom : Plant() {

    init {
        image = 9
        plantName = "Mistbloom"
    }

    override fun activate(ch: Char?) {
        super.activate(ch)

        // Blind enemies in a 3x3 area
        for (n in Level.NEIGHBOURS9) {
            val c = pos + n
            if (c >= 0 && c < Level.LENGTH) {
                if (Dungeon.visible[c]) {
                    CellEmitter.get(c).burst(SmokeParticle.FACTORY, 4)
                }
                val target = Actor.findChar(c)
                if (target != null && target !== Dungeon.hero) {
                    Buffs.prolong(target, Blindness::class.java, 5f)
                }
            }
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        init {
            plantName = "Mistbloom"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_MISTBLOOM
            plantClass = Mistbloom::class.java
            alchemyClass = PotionOfInvisibility::class.java
        }

        override fun desc(): String {
            return TXT_DESC
        }
    }

    companion object {
        private const val TXT_DESC =
            "When disturbed, the Mistbloom releases a thick cloud of mist that blinds nearby creatures."
    }
}
