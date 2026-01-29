package com.watabou.pixeldungeon.items.potions
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ParalyticGas
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.GasesImmunity
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.BArray
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.PathFinder
class PotionOfPurity : Potion() {
    init {
        name = "Potion of Purification"
    }
    override fun shatter(cell: Int) {
        PathFinder.buildDistanceMap(cell, BArray.not(Level.losBlocking, null), DISTANCE)
        val level = Dungeon.level ?: return
        val distance = PathFinder.distance ?: return
        var procd = false
        val blobs = arrayOf(
            level.blobs[ToxicGas::class.java],
            level.blobs[ParalyticGas::class.java]
        )
        for (j in blobs.indices) {
            val blob = blobs[j] ?: continue
            for (i in 0 until Level.LENGTH) {
                if (distance[i] < Int.MAX_VALUE) {
                    val value = blob.cur[i]
                    if (value > 0) {
                        blob.cur[i] = 0
                        blob.volume -= value
                        procd = true
                        if (Dungeon.visible[i]) {
                            CellEmitter.get(i).burst(Speck.factory(Speck.DISCOVER), 1)
                        }
                    }
                }
            }
        }
        val heroPos = Dungeon.hero?.pos ?: return
        val heroAffected = distance[heroPos] < Int.MAX_VALUE
        if (procd) {
            if (Dungeon.visible[cell]) {
                splash(cell)
                Sample.play(Assets.SND_SHATTER)
            }
            setKnown()
            if (heroAffected) {
                GLog.p(TXT_FRESHNESS)
            }
        } else {
            super.shatter(cell)
            if (heroAffected) {
                GLog.i(TXT_FRESHNESS)
                setKnown()
            }
        }
    }
    override fun apply(hero: Hero) {
        GLog.w(TXT_NO_SMELL)
        Buffs.prolong(hero, GasesImmunity::class.java, GasesImmunity.DURATION)
        setKnown()
    }
    override fun desc(): String {
        return "This reagent will quickly neutralize all harmful gases in the area of effect. " +
                "Drinking it will give you a temporary immunity to such gases."
    }
    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
    companion object {
        private const val TXT_FRESHNESS = "You feel uncommon freshness in the air."
        private const val TXT_NO_SMELL = "You've stopped sensing any smells!"
        private const val DISTANCE = 2
    }
}
