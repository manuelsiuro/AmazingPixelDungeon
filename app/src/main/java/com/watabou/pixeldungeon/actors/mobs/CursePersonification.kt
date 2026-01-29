package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.mobs.npcs.Ghost
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.CursePersonificationSprite
import java.util.*
class CursePersonification : Mob() {
    init {
        name = "curse personification"
        spriteClass = CursePersonificationSprite::class.java
        HT = 10 + Dungeon.depth * 3
        HP = HT
        defenseSkill = 10 + Dungeon.depth
        EXP = 3
        maxLvl = 5
        state = HUNTING
        baseSpeed = 0.5f
        flying = true
    }
    override fun damageRoll(): Int {
        return com.watabou.utils.Random.NormalIntRange(3, 5)
    }
    override fun attackSkill(target: Char?): Int {
        return 10 + Dungeon.depth
    }
    override fun dr(): Int {
        return 1
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        for (i in Level.NEIGHBOURS8.indices) {
            val ofs = Level.NEIGHBOURS8[i]
            if (enemy.pos - pos == ofs) {
                val newPos = enemy.pos + ofs
                if ((Level.passable[newPos] || Level.avoid[newPos]) && Actor.findChar(newPos) == null) {
                    Actor.addDelayed(Pushing(enemy, enemy.pos, newPos), -1f)
                    enemy.pos = newPos
                    if (enemy is Mob) {
                        Dungeon.level?.mobPress(enemy)
                    } else {
                        Dungeon.level?.press(newPos, enemy)
                    }
                }
                break
            }
        }
        return super.attackProc(enemy, damage)
    }
    override fun act(): Boolean {
        if (HP > 0 && HP < HT) {
            HP++
        }
        return super.act()
    }
    override fun die(src: Any?) {
        val ghost = Ghost()
        ghost.state = ghost.PASSIVE
        Ghost.replace(this, ghost)
    }
    override fun description(): String {
        return "This creature resembles the sad ghost, but it swirls with darkness. Its face bears an expression of despair."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>(
            Death::class.java,
            Terror::class.java,
            Paralysis::class.java,
            Roots::class.java
        )
    }
}
