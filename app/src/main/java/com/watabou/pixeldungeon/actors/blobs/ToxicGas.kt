package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
class ToxicGas : Blob(), Hero.Doom {
    override fun evolve() {
        super.evolve()
        val levelDamage = 5 + Dungeon.depth * 5
        var ch: com.watabou.pixeldungeon.actors.Char? = null
        for (i in 0 until LENGTH) {
            if (cur[i] > 0 && Actor.findChar(i).also { ch = it } != null) {
                var damage = (ch!!.HT + levelDamage) / 40
                if (Random.Int(40) < (ch!!.HT + levelDamage) % 40) {
                    damage++
                }
                ch!!.damage(damage, this)
            }
        }
        val blob = Dungeon.level!!.blobs[ParalyticGas::class.java]
        if (blob != null) {
            val par = blob.cur
            for (i in 0 until LENGTH) {
                val t = cur[i]
                val p = par[i]
                if (p >= t) {
                    volume -= t
                    cur[i] = 0
                } else {
                    blob.volume -= p
                    par[i] = 0
                }
            }
        }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(Speck.factory(Speck.TOXIC), 0.6f)
    }
    override fun tileDesc(): String {
        return "A greenish cloud of toxic gas is swirling here."
    }
    override fun onDeath() {
        Badges.validateDeathFromGas()
        Dungeon.fail(Utils.format(ResultDescriptions.GAS, Dungeon.depth))
        GLog.n("You died from a toxic gas..")
    }
}
