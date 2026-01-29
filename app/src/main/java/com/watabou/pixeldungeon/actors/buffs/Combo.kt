package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
class Combo : Buff() {
    var count = 0
    override fun icon(): Int {
        return BuffIndicator.COMBO
    }
    override fun toString(): String {
        return "Combo"
    }
    @Suppress("UNUSED_PARAMETER")
    fun hit(enemy: Char, damage: Int): Int {
        count++
        return if (count >= 3) {
            Badges.validateMasteryCombo(count)
            GLog.p(TXT_COMBO, count)
            postpone(1.41f - count / 10f)
            (damage * (count - 2) / 5f).toInt()
        } else {
            postpone(1.1f)
            0
        }
    }
    override fun act(): Boolean {
        detach()
        return true
    }
    companion object {
        private const val TXT_COMBO = "%d hit combo!"
    }
}
