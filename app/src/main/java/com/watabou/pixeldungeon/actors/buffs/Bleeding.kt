package com.watabou.pixeldungeon.actors.buffs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.effects.Splash
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.PointF
import com.watabou.utils.Random
import kotlin.math.min
class Bleeding : Buff() {
    var level: Int = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        level = bundle.getInt(LEVEL)
    }
    fun set(level: Int) {
        this.level = level
    }
    override fun icon(): Int {
        return BuffIndicator.BLEEDING
    }
    override fun toString(): String {
        return "Bleeding"
    }
    override fun act(): Boolean {
        val target = target ?: return true
        if (target.isAlive) {
            level = Random.Int(level / 2, level)
            if (level > 0) {
                target.damage(level, this)
                target.sprite?.let { sprite ->
                    if (sprite.visible) {
                        Splash.at(
                            sprite.center(), -PointF.PI / 2, PointF.PI / 6,
                            sprite.blood(), min(10 * level / target.HT, 10)
                        )
                    }
                }
                if (target == Dungeon.hero && !target.isAlive) {
                    Dungeon.fail(Utils.format(ResultDescriptions.BLEEDING, Dungeon.depth))
                    GLog.n("You bled to death...")
                }
                spend(TICK)
            } else {
                detach()
            }
        } else {
            detach()
        }
        return true
    }
    companion object {
        private const val LEVEL = "level"
    }
}
