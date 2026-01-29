package com.watabou.pixeldungeon.items.scrolls
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Invisibility
import com.watabou.pixeldungeon.actors.buffs.Rage
import com.watabou.pixeldungeon.actors.mobs.Mimic
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.GLog
class ScrollOfChallenge : Scroll() {
    init {
        name = "Scroll of Challenge"
    }
    override fun doRead() {
        val hero = curUser ?: return
        val level = Dungeon.level ?: return
        for (mob in level.mobs.toList()) {
            mob.beckon(hero.pos)
            if (Dungeon.visible[mob.pos]) {
                Buffs.affect(mob, Rage::class.java, Level.distance(hero.pos, mob.pos).toFloat())
            }
        }
        for (heap in level.heaps.values().toList()) {
            if (heap.type == Heap.Type.MIMIC) {
                val m = Mimic.spawnAt(heap.pos, heap.items)
                if (m != null) {
                    m.beckon(hero.pos)
                    heap.destroy()
                }
            }
        }
        GLog.w("The scroll emits a challenging roar that echoes throughout the dungeon!")
        setKnown()
        hero.sprite?.centerEmitter()?.start(Speck.factory(Speck.SCREAM), 0.3f, 3)
        Sample.play(Assets.SND_CHALLENGE)
        Invisibility.dispel()
        readAnimation()
    }
    override fun desc(): String {
        return "When read aloud, this scroll will unleash a challenging roar " +
                "that will awaken all monsters and alert them to the reader's location."
    }
}
