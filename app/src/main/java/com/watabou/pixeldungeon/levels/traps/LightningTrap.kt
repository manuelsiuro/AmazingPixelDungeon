package com.watabou.pixeldungeon.levels.traps
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.effects.particles.SparkParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
import kotlin.math.max
object LightningTrap {
    private const val name = "lightning trap"
    val LIGHTNING = Electricity()
    fun trigger(pos: Int, ch: Char?) {
        if (ch != null) {
            ch.damage(max(1, Random.Int(ch.HP / 3, 2 * ch.HP / 3)), LIGHTNING)
            if (ch === Dungeon.hero) {
                Camera.main?.shake(2f, 0.3f)
                if (!ch.isAlive) {
                    Dungeon.fail(Utils.format(ResultDescriptions.TRAP, name, Dungeon.depth))
                    GLog.n("You were killed by a discharge of a lightning trap...")
                } else {
                    ch.belongings.charge(false)
                }
            }
            val points = IntArray(2)
            points[0] = pos - Level.WIDTH
            points[1] = pos + Level.WIDTH
            ch.sprite?.parent?.add(Lightning(points, 2, null))
            points[0] = pos - 1
            points[1] = pos + 1
            ch.sprite?.parent?.add(Lightning(points, 2, null))
        }
        CellEmitter.center(pos).burst(SparkParticle.FACTORY, Random.IntRange(3, 4))
    }
    class Electricity
}
