package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Sleep
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfLullaby : Scroll() {
    init {
        name = "Scroll of Lullaby"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val level = Dungeon.level ?: return
        hero.sprite?.centerEmitter()?.start(Speck.factory(Speck.NOTE), 0.3f, 5)
        Sample.play(Assets.SND_LULLABY)
        Invisibility.dispel()
        var count = 0
        var affected: Mob? = null
        for (mob in level.mobs.toList()) {
            if (Level.fieldOfView[mob.pos]) {
                Buffs.affect(mob, Sleep::class.java)
                if (mob.buff(Sleep::class.java) != null) {
                    affected = mob
                    count++
                }
            }
        }
        when (count) {
            0 -> GLog.i("The scroll utters a soothing melody.")
            1 -> GLog.i("The scroll utters a soothing melody and the " + (affected?.name ?: "enemy") + " falls asleep!")
            else -> GLog.i("The scroll utters a soothing melody and the monsters fall asleep!")
        }
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "A soothing melody will put all creatures in your field of view into a deep sleep, " +
                "giving you a chance to flee or make a surprise attack on them."
    }
    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
}
