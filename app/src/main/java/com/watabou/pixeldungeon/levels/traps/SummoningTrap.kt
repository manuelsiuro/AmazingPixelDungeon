package com.watabou.pixeldungeon.levels.traps
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Bestiary
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.utils.Random
import java.util.ArrayList
object SummoningTrap {
    private const val DELAY = 2f
    private val DUMMY = object : Mob() {}
    fun trigger(pos: Int, c: Char?) {
        if (Dungeon.bossLevel()) {
            return
        }
        if (c != null) {
            Actor.occupyCell(c)
        }
        var nMobs = 1
        if (Random.Int(2) == 0) {
            nMobs++
            if (Random.Int(2) == 0) {
                nMobs++
            }
        }
        val candidates = ArrayList<Int>()
        for (i in Level.NEIGHBOURS8.indices) {
            val p = pos + Level.NEIGHBOURS8[i]
            if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                candidates.add(p)
            }
        }
        val respawnPoints = ArrayList<Int>()
        while (nMobs > 0 && candidates.size > 0) {
            val index = Random.index(candidates)
            DUMMY.pos = candidates[index]
            Actor.occupyCell(DUMMY)
            respawnPoints.add(candidates.removeAt(index))
            nMobs--
        }
        for (point in respawnPoints) {
            val mob = Bestiary.mob(Dungeon.depth)
            if (mob != null) {
                mob.state = mob.WANDERING
                GameScene.add(mob, DELAY)
                WandOfBlink.appear(mob, point)
            }
        }
    }
}
