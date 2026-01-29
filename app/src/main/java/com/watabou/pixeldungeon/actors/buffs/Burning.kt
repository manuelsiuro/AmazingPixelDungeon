package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Fire
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.Thief
import com.watabou.pixeldungeon.effects.particles.ElmoParticle
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.food.ChargrilledMeat
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.rings.RingOfElements.Resistance
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class Burning : Buff(), Hero.Doom {
    private var left = 0f
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEFT, left)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        left = bundle.getFloat(LEFT)
    }
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            if (target is Hero) {
                Buffs.prolong(target, Light::class.java, TICK * 1.01f)
            }
            target.damage(Random.Int(1, 5), this)
            if (target is Hero) {
                val item = target.belongings.randomUnequipped()
                if (item is Scroll) {
                    val detachedItem = item.detach(target.belongings.backpack)
                    GLog.w(TXT_BURNS_UP, detachedItem.toString())
                    Heap.burnFX(target.pos)
                } else if (item is MysteryMeat) {
                    val detachedItem = item.detach(target.belongings.backpack)
                    val steak = ChargrilledMeat()
                    if (!steak.collect(target.belongings.backpack)) {
                        Dungeon.level?.drop(steak, target.pos)?.sprite?.drop()
                    }
                    GLog.w(TXT_BURNS_UP, detachedItem.toString())
                    Heap.burnFX(target.pos)
                }
            } else if (target is Thief && target.item is Scroll) {
                target.item = null
                target.sprite?.emitter()?.burst(ElmoParticle.FACTORY, 6)
            }
        } else {
            detach()
        }
        if (Level.flamable[target.pos]) {
            Blob.seed(target.pos, 4, Fire::class.java)?.let { GameScene.add(it) }
        }
        spend(TICK)
        left -= TICK
        if (left <= 0 ||
            Random.Float() > (2 + target.HP.toFloat() / target.HT) / 3 ||
            (Level.water[target.pos] && !target.flying)
        ) {
            detach()
        }
        return true
    }
    fun reignite(ch: Char) {
        left = duration(ch)
    }
    override fun icon(): Int {
        return BuffIndicator.FIRE
    }
    override fun toString(): String {
        return "Burning"
    }
    override fun onDeath() {
        Badges.validateDeathFromFire()
        Dungeon.fail(Utils.format(ResultDescriptions.BURNING, Dungeon.depth))
        GLog.n(TXT_BURNED_TO_DEATH)
    }
    companion object {
        private const val TXT_BURNS_UP = "%s burns up!"
        private const val TXT_BURNED_TO_DEATH = "You burned to death..."
        private const val DURATION = 8f
        private const val LEFT = "left"
        fun duration(ch: Char): Float {
            val r = ch.buff(Resistance::class.java)
            return if (r != null) r.durationFactor() * DURATION else DURATION
        }
    }
}
