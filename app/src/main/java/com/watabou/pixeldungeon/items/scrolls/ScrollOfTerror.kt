package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfTerror : Scroll() {
    init {
        name = "Scroll of Terror"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val heroSprite = hero.sprite ?: return
        val level = Dungeon.level ?: return
        Flare(5, 32f).color(0xFF0000, true).show(heroSprite, 2f)
        Sample.play(Assets.SND_READ)
        Invisibility.dispel()
        var count = 0
        var affected: Mob? = null
        for (mob in level.mobs.toList()) {
            if (Level.fieldOfView[mob.pos]) {
                Buffs.affect(mob, Terror::class.java, Terror.DURATION)?.`object` = hero.id()
                count++
                affected = mob
            }
        }
        when (count) {
            0 -> GLog.i("The scroll emits a brilliant flash of red light")
            1 -> GLog.i("The scroll emits a brilliant flash of red light and the " + (affected?.name ?: "enemy") + " flees!")
            else -> GLog.i("The scroll emits a brilliant flash of red light and the monsters flee!")
        }
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "A flash of red light will overwhelm all creatures in your field of view with terror, " +
                "and they will turn and flee. Attacking a fleeing enemy will dispel the effect."
    }
    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
}
