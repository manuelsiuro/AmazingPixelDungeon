package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Challenges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Statistics
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Amok
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Sleep
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroSubClass
import com.watabou.pixeldungeon.effects.Flare
import com.watabou.pixeldungeon.effects.Wound
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
abstract class Mob : Char() {
    var SLEEPING: AiState = Sleeping()
    var HUNTING: AiState = Hunting()
    var WANDERING: AiState = Wandering()
    var FLEEING: AiState = Fleeing()
    var PASSIVE: AiState = Passive()
    var state: AiState = SLEEPING
    var spriteClass: Class<out CharSprite>? = null
    protected var target = -1
    protected var defenseSkill = 0
    protected var EXP = 1
    protected var maxLvl = 30
    protected var enemy: Char? = null
    protected var enemySeen = false
    protected var alerted = false
    var hostile = true
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        val stateTag = when (state) {
            SLEEPING -> TAG_SLEEPING
            WANDERING -> TAG_WANDERING
            HUNTING -> TAG_HUNTING
            FLEEING -> TAG_FLEEING
            PASSIVE -> TAG_PASSIVE
            else -> ""
        }
        if (stateTag.isNotEmpty()) {
            bundle.put(STATE, stateTag)
        }
        bundle.put(TARGET, target)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        when (bundle.getString(STATE)) {
            TAG_SLEEPING -> state = SLEEPING
            TAG_WANDERING -> state = WANDERING
            TAG_HUNTING -> state = HUNTING
            TAG_FLEEING -> state = FLEEING
            TAG_PASSIVE -> state = PASSIVE
        }
        target = bundle.getInt(TARGET)
    }
    open fun sprite(): CharSprite? {
        return try {
            spriteClass?.getDeclaredConstructor()?.newInstance()
        } catch (e: Exception) {
            null
        }
    }
    override fun act(): Boolean {
        super.act()
        val justAlerted = alerted
        alerted = false
        sprite?.hideAlert()
        if (paralysed) {
            enemySeen = false
            spend(TICK)
            return true
        }
        enemy = chooseEnemy()
        val currentEnemy = enemy
        val enemyInFOV = (currentEnemy != null && currentEnemy.isAlive &&
                Level.fieldOfView[currentEnemy.pos] && currentEnemy.invisible <= 0)
        return state.act(enemyInFOV, justAlerted)
    }
    protected open fun chooseEnemy(): Char? {
        val level = Dungeon.level ?: return Dungeon.hero
        if (buff(Amok::class.java) != null) {
            if (enemy == Dungeon.hero || enemy == null) {
                val enemies = mutableListOf<Mob>()
                for (mob in level.mobs) {
                    if (mob != this && Level.fieldOfView[mob.pos]) {
                        enemies.add(mob)
                    }
                }
                if (enemies.isNotEmpty()) {
                    return Random.element(enemies)
                }
            }
        }
        val terror = buff(Terror::class.java)
        if (terror != null) {
            val source = Actor.findById(terror.`object`) as Char?
            if (source != null) {
                return source
            }
        }
        val currentEnemy = enemy
        return if (currentEnemy != null && currentEnemy.isAlive) currentEnemy else Dungeon.hero
    }
    protected open fun moveSprite(from: Int, to: Int): Boolean {
        val s = sprite ?: return true
        val visible = Dungeon.visible
        return if (s.isVisible() && (visible[from] || visible[to])) {
            s.move(from, to)
            true
        } else {
            s.place(to)
            true
        }
    }
    override fun add(buff: Buff) {
        super.add(buff)
        when (buff) {
            is Amok -> {
                sprite?.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
                state = HUNTING
            }
            is Terror -> state = FLEEING
            is Sleep -> {
                sprite?.let {
                    Flare(4, 32f).color(0x44ffff, true).show(it, 2f)
                }
                state = SLEEPING
                postpone(Sleep.SWS)
            }
        }
    }
    override fun remove(buff: Buff) {
        super.remove(buff)
        if (buff is Terror) {
            sprite?.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
            state = HUNTING
        }
    }
    protected open fun canAttack(enemy: Char): Boolean {
        return Level.adjacent(pos, enemy.pos) && !isCharmedBy(enemy)
    }
    protected open fun getCloser(target: Int): Boolean {
        if (rooted) {
            return false
        }
        val step = Dungeon.findPath(
            this, pos, target,
            Level.passable,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }
    protected open fun getFurther(target: Int): Boolean {
        val step = Dungeon.flee(
            this, pos, target,
            Level.passable,
            Level.fieldOfView
        )
        return if (step != -1) {
            move(step)
            true
        } else {
            false
        }
    }
    override fun move(step: Int) {
        super.move(step)
        if (!flying) {
            Dungeon.level?.mobPress(this)
        }
    }
    protected open fun attackDelay(): Float {
        return 1f
    }
    protected open fun doAttack(enemy: Char): Boolean {
        val visible = Dungeon.visible[pos]
        if (visible) {
            sprite?.attack(enemy.pos)
        } else {
            attack(enemy)
        }
        spend(attackDelay())
        return !visible
    }
    override fun onAttackComplete() {
        enemy?.let { attack(it) }
        super.onAttackComplete()
    }
    override fun defenseSkill(enemy: Char?): Int {
        return if (enemySeen && !paralysed) defenseSkill else 0
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        var dmg = damage
        if (!enemySeen && enemy == Dungeon.hero && (enemy as Hero).subClass == HeroSubClass.ASSASSIN) {
            dmg += Random.Int(1, dmg)
            Wound.hit(this)
        }
        return dmg
    }
    open fun aggro(ch: Char) {
        enemy = ch
    }
    override fun damage(dmg: Int, src: Any?) {
        Terror.recover(this)
        if (state == SLEEPING) {
            state = WANDERING
        }
        alerted = true
        super.damage(dmg, src)
    }
    override fun destroy() {
        super.destroy()
        Dungeon.level?.mobs?.remove(this)
        val hero = Dungeon.hero ?: return
        if (hero.isAlive) {
            if (hostile) {
                Statistics.enemiesSlain++
                Badges.validateMonstersSlain()
                Statistics.qualifiedForNoKilling = false
                if (Dungeon.nightMode) {
                    Statistics.nightHunt++
                } else {
                    Statistics.nightHunt = 0
                }
                Badges.validateNightHunter()
            }
            val exp = exp()
            if (exp > 0) {
                hero.sprite?.showStatus(CharSprite.POSITIVE, TXT_EXP, exp)
                hero.earnExp(exp)
            }
        }
    }
    open fun exp(): Int {
        val hero = Dungeon.hero ?: return 0
        return if (hero.lvl <= maxLvl) EXP else 0
    }
    override fun die(src: Any?) {
        super.die(src)
        val hero = Dungeon.hero ?: return
        if (hero.lvl <= maxLvl + 2) {
            dropLoot()
        }
        if (hero.isAlive && !Dungeon.visible[pos]) {
            GLog.i(TXT_DIED)
        }
    }
    protected var loot: Any? = null
    protected var lootChance = 0f
    @Suppress("UNCHECKED_CAST")
    protected open fun dropLoot() {
        if (loot != null && Random.Float() < lootChance) {
            var item: Item? = null
            when (loot) {
                is Generator.Category -> {
                    item = Generator.random(loot as Generator.Category)
                }
                is Class<*> -> {
                    item = Generator.random(loot as Class<out Item>)
                }
                is Item -> {
                    item = loot as Item
                }
            }
            item?.let {
                Dungeon.level?.drop(it, pos)?.sprite?.drop()
            }
        }
    }
    open fun reset(): Boolean {
        return false
    }
    open fun beckon(cell: Int) {
        notice()
        if (state != HUNTING) {
            state = WANDERING
        }
        target = cell
    }
    open fun description(): String {
        return "Real description is coming soon!"
    }
    open fun notice() {
        sprite?.showAlert()
    }
    open fun yell(str: String) {
        GLog.n("%s: \"%s\" ", name, str)
    }
    interface AiState {
        fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean
        fun status(): String
    }
    private inner class Sleeping : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            val currentEnemy = enemy
            if (enemyInFOV && currentEnemy != null && Random.Int(distance(currentEnemy) + currentEnemy.stealth() + (if (currentEnemy.flying) 2 else 0)) == 0) {
                enemySeen = true
                notice()
                state = HUNTING
                target = currentEnemy.pos
                if (Dungeon.isChallenged(Challenges.SWARM_INTELLIGENCE)) {
                    Dungeon.level?.mobs?.forEach { mob ->
                        if (mob != this@Mob) {
                            mob.beckon(target)
                        }
                    }
                }
                spend(TIME_TO_WAKE_UP)
            } else {
                enemySeen = false
                spend(TICK)
            }
            return true
        }
        override fun status(): String {
            return Utils.format("This %s is sleeping", name)
        }
    }
    private inner class Wandering : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            val currentEnemy = enemy
            if (enemyInFOV && currentEnemy != null && (justAlerted || Random.Int(distance(currentEnemy) / 2 + currentEnemy.stealth()) == 0)) {
                enemySeen = true
                notice()
                state = HUNTING
                target = currentEnemy.pos
            } else {
                enemySeen = false
                val oldPos = pos
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed())
                    return moveSprite(oldPos, pos)
                } else {
                    target = Dungeon.level?.randomDestination() ?: -1
                    spend(TICK)
                }
            }
            return true
        }
        override fun status(): String {
            return Utils.format("This %s is wandering", name)
        }
    }
    private inner class Hunting : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            val currentEnemy = enemy
            return if (enemyInFOV && currentEnemy != null && canAttack(currentEnemy)) {
                doAttack(currentEnemy)
            } else {
                if (enemyInFOV && currentEnemy != null) {
                    target = currentEnemy.pos
                }
                val oldPos = pos
                if (target != -1 && getCloser(target)) {
                    spend(1 / speed())
                    moveSprite(oldPos, pos)
                } else {
                    spend(TICK)
                    state = WANDERING
                    target = Dungeon.level?.randomDestination() ?: -1
                    true
                }
            }
        }
        override fun status(): String {
            return Utils.format("This %s is hunting", name)
        }
    }
    protected open inner class Fleeing : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = enemyInFOV
            if (enemyInFOV) {
                enemy?.let { target = it.pos }
            }
            val oldPos = pos
            return if (target != -1 && getFurther(target)) {
                spend(1 / speed())
                moveSprite(oldPos, pos)
            } else {
                spend(TICK)
                nowhereToRun()
                true
            }
        }
        protected open fun nowhereToRun() {}
        override fun status(): String {
            return Utils.format("This %s is fleeing", name)
        }
    }
    private inner class Passive : AiState {
        override fun act(enemyInFOV: Boolean, justAlerted: Boolean): Boolean {
            enemySeen = false
            spend(TICK)
            return true
        }
        override fun status(): String {
            return Utils.format("This %s is passive", name)
        }
    }
    companion object {
        private const val TXT_DIED = "You hear something died in the distance"
        const val TXT_ECHO = "echo of "
        const val TXT_NOTICE1 = "?!"
        const val TXT_RAGE = "#$%^"
        const val TXT_EXP = "%+dEXP"
        const val TXT_KILL = "%s killed you..."
        protected const val TIME_TO_WAKE_UP = 1f
        private const val STATE = "state"
        private const val TARGET = "target"
        private const val TAG_SLEEPING = "SLEEPING"
        private const val TAG_WANDERING = "WANDERING"
        private const val TAG_HUNTING = "HUNTING"
        private const val TAG_FLEEING = "FLEEING"
        private const val TAG_PASSIVE = "PASSIVE"
    }
}
