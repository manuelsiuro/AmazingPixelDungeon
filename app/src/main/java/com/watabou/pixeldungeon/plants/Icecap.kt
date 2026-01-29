package com.watabou.pixeldungeon.plants
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.blobs.Freezing
import com.watabou.pixeldungeon.items.potions.PotionOfFrost
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.utils.PathFinder
class Icecap : Plant() {
    init {
        image = 1
        plantName = "Icecap"
    }
    override fun activate(ch: Char?) {
        super.activate(ch)
        PathFinder.buildDistanceMap(pos, BArray.not(Level.losBlocking, null), 1)
        val fire = Dungeon.level!!.blobs[Fire::class.java] as Fire?
        for (i in 0 until Level.LENGTH) {
            if (PathFinder.distance!![i] < Int.MAX_VALUE) {
                Freezing.affect(i, fire)
            }
        }
    }
    override fun desc(): String {
        return TXT_DESC
    }
    class Seed : Plant.Seed() {
        init {
            plantName = "Icecap"
            name = "seed of " + plantName
            image = ItemSpriteSheet.SEED_ICECAP
            plantClass = Icecap::class.java
            alchemyClass = PotionOfFrost::class.java
        }
        override fun desc(): String {
            return TXT_DESC
        }
    }
    companion object {
        private const val TXT_DESC = "Upon touching an Icecap excretes a pollen, which freezes everything in its vicinity."
    }
}
