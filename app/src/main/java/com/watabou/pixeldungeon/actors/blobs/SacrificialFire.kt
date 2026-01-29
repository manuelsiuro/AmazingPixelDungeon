package com.watabou.pixeldungeon.actors.blobs
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.FlavourBuff
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.Wound
import com.watabou.pixeldungeon.effects.particles.SacrificialParticle
import com.watabou.pixeldungeon.items.scrolls.ScrollOfWipeOut
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class SacrificialFire : Blob() {
    protected var pos: Int = 0
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        for (i in 0 until LENGTH) {
            if (cur[i] > 0) {
                pos = i
                break
            }
        }
    }
    override fun evolve() {
        volume = cur[pos].also { off[pos] = it }
        val ch = Actor.findChar(pos)
        if (ch != null) {
            if (Dungeon.visible[pos] && ch.buff(Marked::class.java) == null) {
                ch.sprite?.emitter()?.burst(SacrificialParticle.FACTORY, 20)
                Sample.play(Assets.SND_BURNING)
            }
            Buffs.prolong(ch, Marked::class.java, Marked.DURATION)
        }
        if (Dungeon.visible[pos]) {
            Journal.add(Journal.Feature.SACRIFICIAL_FIRE)
        }
    }
    override fun seed(cell: Int, amount: Int) {
        cur[pos] = 0
        pos = cell
        volume = amount.also { cur[pos] = it }
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.pour(SacrificialParticle.FACTORY, 0.04f)
    }
    override fun tileDesc(): String {
        return "Sacrificial fire burns here. Every creature touched by this fire is marked as an offering for the spirits of the dungeon."
    }
    class Marked : FlavourBuff() {
        override fun icon(): Int {
            return BuffIndicator.SACRIFICE
        }
        override fun toString(): String {
            return "Marked for sacrifice"
        }
        override fun detach() {
            if (!target!!.isAlive) {
                sacrifice(target!!)
            }
            super.detach()
        }
        companion object {
            const val DURATION = 5f
        }
    }
    companion object {
        private const val TXT_WORTHY = "\"Your sacrifice is worthy...\" "
        private const val TXT_UNWORTHY = "\"Your sacrifice is unworthy...\" "
        private const val TXT_REWARD = "\"Your sacrifice is worthy and so you are!\" "
        fun sacrifice(ch: Char) {
            Wound.hit(ch)
            val fire = Dungeon.level!!.blobs[SacrificialFire::class.java] as SacrificialFire?
            if (fire != null) {
                var exp = 0
                if (ch is Mob) {
                    exp = ch.exp() * Random.IntRange(1, 3)
                } else if (ch is Hero) {
                    exp = ch.maxExp()
                }
                if (exp > 0) {
                    val volume = fire.volume - exp
                    if (volume > 0) {
                        fire.seed(fire.pos, volume)
                        GLog.w(TXT_WORTHY)
                    } else {
                        fire.seed(fire.pos, 0)
                        Journal.remove(Journal.Feature.SACRIFICIAL_FIRE)
                        GLog.w(TXT_REWARD)
                        val flare = Flare(7, 32f)
                        flare.color(0x66FFFF, true)
                        flare.show(ch.sprite!!.parent!!, DungeonTilemap.tileCenterToWorld(fire.pos), 2f)
                        GameScene.effect(flare)
                        Dungeon.level!!.drop(ScrollOfWipeOut(), fire.pos).sprite?.drop()
                    }
                } else {
                    GLog.w(TXT_UNWORTHY)
                }
            }
        }
    }
}
