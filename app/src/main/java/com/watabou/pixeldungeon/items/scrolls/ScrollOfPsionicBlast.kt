package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Random
class ScrollOfPsionicBlast : Scroll() {
    init {
        name = "Scroll of Psionic Blast"
    }
    override fun doRead() {
        GameScene.flash(0xFFFFFF)
        Sample.play(Assets.SND_BLAST)
        Invisibility.dispel()
        Dungeon.level?.mobs?.toList()?.forEach { mob ->
            if (Level.fieldOfView[mob.pos]) {
                Buffs.prolong(mob, Blindness::class.java, Random.Int(3, 6).toFloat())
                mob.damage(Random.IntRange(1, mob.HT * 2 / 3), this)
            }
        }
        curUser?.let { Buffs.prolong(it, Blindness::class.java, Random.Int(3, 6).toFloat()) }
        Dungeon.observe()
        setKnown()
        readAnimation()
    }
    override fun desc(): String {
        return "This scroll contains destructive energy, that can be psionically channeled to inflict a " +
                "massive damage to all creatures within a field of view. An accompanying flash of light will " +
                "temporarily blind everybody in the area of effect including the reader of the scroll."
    }
    override fun price(): Int {
        return if (isKnown) 80 * quantity else super.price()
    }
}
