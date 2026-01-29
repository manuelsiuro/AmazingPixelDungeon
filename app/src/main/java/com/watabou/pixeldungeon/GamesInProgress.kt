package com.watabou.pixeldungeon
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.utils.Bundle
import java.util.HashMap
object GamesInProgress {
    private val state = HashMap<HeroClass, Info?>()
    fun check(cl: HeroClass): Info? {
        if (state.containsKey(cl)) {
            return state[cl]
        } else {
            var info: Info?
            try {
                val bundle = Dungeon.gameBundle(Dungeon.gameFile(cl))!!
                info = Info()
                Dungeon.preview(info, bundle)
            } catch (e: Exception) {
                info = null
            }
            state[cl] = info
            return info
        }
    }
    fun set(cl: HeroClass, depth: Int, level: Int, challenges: Boolean) {
        val info = Info()
        info.depth = depth
        info.level = level
        info.challenges = challenges
        state[cl] = info
    }
    fun setUnknown(cl: HeroClass) {
        state.remove(cl)
    }
    fun delete(cl: HeroClass) {
        state[cl] = null
    }
    class Info {
        var depth: Int = 0
        var level: Int = 0
        var challenges: Boolean = false
    }
}
