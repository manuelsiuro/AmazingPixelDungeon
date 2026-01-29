package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.PiranhaSprite
import com.watabou.utils.Random
import java.util.*
class Piranha : Mob() {
    init {
        name = "giant piranha"
        spriteClass = PiranhaSprite::class.java
        baseSpeed = 2f
        EXP = 0
        HP = 10 + Dungeon.depth * 5
        HT = HP
        defenseSkill = 10 + Dungeon.depth * 2
    }
    override fun act(): Boolean {
        return if (!Level.water[pos]) {
            die(null)
            true
        } else {
            super.act()
        }
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(Dungeon.depth, 4 + Dungeon.depth * 2)
    }
    override fun attackSkill(target: Char?): Int {
        return 20 + Dungeon.depth * 2
    }
    override fun dr(): Int {
        return Dungeon.depth
    }
    override fun die(src: Any?) {
        Dungeon.level?.drop(MysteryMeat(), pos)?.sprite?.drop()
        super.die(src)
        Statistics.piranhasKilled++
        Badges.validatePiranhasKilled()
    }
    override fun reset(): Boolean {
        return true
    }
    override fun getCloser(target: Int): Boolean {
        if (rooted) {
            return false
        }
        val step = Dungeon.findPath(this, pos, target, Level.water, Level.fieldOfView)
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }
    override fun getFurther(target: Int): Boolean {
        val step = Dungeon.flee(this, pos, target, Level.water, Level.fieldOfView)
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }
    override fun description(): String {
        return "These carnivorous fish are not natural inhabitants of underground pools. They were bred specifically to protect flooded treasure vaults."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>(
            Burning::class.java,
            Paralysis::class.java,
            ToxicGas::class.java,
            Roots::class.java,
            Frost::class.java
        )
    }
}
