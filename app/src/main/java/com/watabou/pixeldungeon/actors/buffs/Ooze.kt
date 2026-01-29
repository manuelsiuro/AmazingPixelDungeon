package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
class Ooze : Buff() {
    var damage = 1
    override fun icon(): Int {
        return BuffIndicator.OOZE
    }
    override fun toString(): String {
        return "Caustic ooze"
    }
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            target.damage(damage, this)
            if (!target.isAlive && target == Dungeon.hero) {
                Dungeon.fail(Utils.format(ResultDescriptions.OOZE, Dungeon.depth))
                GLog.n(TXT_HERO_KILLED, toString())
            }
            spend(TICK)
        }
        if (Level.water[target.pos]) {
            detach()
        }
        return true
    }
    companion object {
        private const val TXT_HERO_KILLED = "%s killed you..."
    }
}
