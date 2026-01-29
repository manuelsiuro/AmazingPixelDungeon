package com.watabou.pixeldungeon.actors.mobs.npcs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.BeeSprite
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Bee : NPC() {
    init {
        name = "golden bee"
        spriteClass = BeeSprite::class.java
        viewDistance = 4
        WANDERING = Wandering()
        flying = true
        state = WANDERING
    }
    private var level: Int = 0
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(LEVEL, level)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        spawn(bundle.getInt(LEVEL))
    }
    fun spawn(level: Int) {
        this.level = level
        HT = (3 + level) * 5
        defenseSkill = 9 + level
    }
    override fun attackSkill(target: Char?): Int {
        return defenseSkill
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(HT / 10, HT / 4)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (enemy is Mob) {
            enemy.aggro(this)
        }
        return damage
    }
    override fun act(): Boolean {
        HP--
        return if (HP <= 0) {
            die(null)
            true
        } else {
            super.act()
        }
    }
    override fun chooseEnemy(): Char? {
        val currentEnemy = enemy
        if (currentEnemy == null || !currentEnemy.isAlive) {
            val level = Dungeon.level ?: return null
            val enemies = HashSet<Mob>()
            for (mob in level.mobs) {
                if (mob.hostile && Level.fieldOfView[mob.pos]) {
                    enemies.add(mob)
                }
            }
            return if (enemies.isNotEmpty()) Random.element(enemies) else null
        } else {
            return currentEnemy
        }
    }
    override fun description(): String {
        return "Despite their small size, golden bees tend to protect their master fiercely. They don't live long though."
    }
    override fun interact() {
        val hero = Dungeon.hero ?: return
        val curPos = pos
        moveSprite(pos, hero.pos)
        move(hero.pos)
        hero.sprite?.move(hero.pos, curPos)
        hero.move(curPos)
        hero.spend(1 / hero.speed())
        hero.busy()
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    private inner class Wandering : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            val currentEnemy = enemy
            if (enemyInFOV && currentEnemy != null) {
                enemySeen = true
                notice()
                state = HUNTING
                target = currentEnemy.pos
            } else {
                enemySeen = false
                val oldPos = pos
                val hero = Dungeon.hero
                if (hero != null && getCloser(hero.pos)) {
                    spend(1 / speed())
                    return moveSprite(oldPos, pos)
                } else {
                    spend(TICK)
                }
            }
            return true
        }
        override fun status(): String {
            return Utils.format("This %s is wandering", name)
        }
    }
    companion object {
        private const val LEVEL = "level"
        private val IMMUNITIES = hashSetOf<Class<*>>(Poison::class.java)
    }
}
