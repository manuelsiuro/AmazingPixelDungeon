package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.potions.PotionOfMindVision
import com.watabou.pixeldungeon.items.scrolls.ScrollOfTeleportation
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Fadeleaf : Plant() {
    init {
        image = 6
        plantName = "Fadeleaf"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        if (ch is Hero) {
            ScrollOfTeleportation.teleportHero(ch)
            ch.curAction = null
        } else if (ch is Mob) {
            var count = 10
            var newPos: Int
            do {
                newPos = Dungeon.level!!.randomRespawnCell()
                if (count-- <= 0) {
                    break
                }
            } while (newPos == -1)
            if (newPos != -1) {
                ch.pos = newPos
                ch.sprite!!.place(ch.pos)
                ch.sprite!!.visible = Dungeon.visible[pos]
            }
        }
        if (Dungeon.visible[pos]) {
            CellEmitter.get(pos).start(Speck.factory(Speck.LIGHT), 0.2f, 3)
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Fadeleaf"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_FADELEAF
            plantClass = Fadeleaf::class.java
            alchemyClass = PotionOfMindVision::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "Touching a Fadeleaf will teleport any creature to a random place on the current level."
    }
}
