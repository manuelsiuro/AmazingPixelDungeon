package com.watabou.pixeldungeon.plants

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.potions.PotionOfMindVision
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Brightcap : Plant() {

    init {
        image = 10
        plantName = "Brightcap"
    }

    override fun activate(ch: Char?) {
        super.activate(ch)

        if (ch != null) {
            Buffs.prolong(ch, Light::class.java, 10f)
        }

        // Reveal traps and hidden doors nearby
        val level = Dungeon.level ?: return
        for (n in Level.NEIGHBOURS9) {
            val c = pos + n
            if (c >= 0 && c < Level.LENGTH) {
                val terr = level.map[c]
                if ((Terrain.flags[terr] and Terrain.SECRET) != 0) {
                    Level.set(c, Terrain.discover(terr))
                    GameScene.updateMap(c)
                }
            }
        }

        if (Dungeon.visible[pos]) {
            CellEmitter.center(pos).burst(Speck.factory(Speck.LIGHT), 5)
        }
    }

    override fun desc(): String {
        return TXT_DESC
    }

    class Seed : Plant.Seed() {
        init {
            plantName = "Brightcap"
            name = "seed of $plantName"
            image = ItemSpriteSheet.SEED_BRIGHTCAP
            plantClass = Brightcap::class.java
            alchemyClass = PotionOfMindVision::class.java
        }

        override fun desc(): String {
            return TXT_DESC
        }
    }

    companion object {
        private const val TXT_DESC =
            "This luminescent mushroom bursts with light when touched, illuminating the area and revealing hidden passages."
    }
}
