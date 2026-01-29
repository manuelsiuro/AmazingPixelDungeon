package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.blobs.Freezing
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.utils.PathFinder
class PotionOfFrost : Potion() {
    init {
        name = "Potion of Frost"
    }
    override fun shatter(cell: Int) {
        PathFinder.buildDistanceMap(cell, BArray.not(Level.losBlocking, null), DISTANCE)
        val level = Dungeon.level ?: return
        val distance = PathFinder.distance ?: return
        val fire = level.blobs[Fire::class.java] as Fire?
        var visible = false
        for (i in 0 until Level.LENGTH) {
            if (distance[i] < Int.MAX_VALUE) {
                visible = Freezing.affect(i, fire) || visible
            }
        }
        if (visible) {
            splash(cell)
            Sample.play(Assets.SND_SHATTER)
            setKnown()
        }
    }
    override fun desc(): String {
        return "Upon exposure to open air, this chemical will evaporate into a freezing cloud, causing " +
                "any creature that contacts it to be frozen in place, unable to act and move."
    }
    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
    companion object {
        private const val DISTANCE = 2
    }
}
