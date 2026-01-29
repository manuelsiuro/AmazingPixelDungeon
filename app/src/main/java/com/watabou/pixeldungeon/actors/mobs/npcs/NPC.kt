package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Random
abstract class NPC : Mob() {
    init {
        HT = 1
        HP = HT
        EXP = 0
        hostile = false
        state = PASSIVE
    }
    protected fun throwItem() {
        val heap: Heap? = Dungeon.level?.heaps?.get(pos)
        if (heap != null) {
            var n: Int
            do {
                n = pos + Level.NEIGHBOURS8[Random.Int(8)]
            } while (!Level.passable[n] && !Level.avoid[n])
            Dungeon.level?.drop(heap.pickUp(), n)?.sprite?.drop(pos)
        }
    }
    override fun beckon(cell: Int) {
    }
    abstract fun interact()
}
