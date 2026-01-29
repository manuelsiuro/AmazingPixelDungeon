package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Weakness
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.WarlockSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Callback
import com.watabou.utils.Random
import java.util.*
class Warlock : Mob(), Callback {
    init {
        name = "dwarf warlock"
        spriteClass = WarlockSprite::class.java
        HT = 70
        HP = HT
        defenseSkill = 18
        EXP = 11
        maxLvl = 21
        loot = Generator.Category.POTION
        lootChance = 0.83f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 20)
    }
    override fun attackSkill(target: Char?): Int {
        return 25
    }
    override fun dr(): Int {
        return 8
    }
    override fun canAttack(enemy: Char): Boolean {
        return Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos
    }
    override fun doAttack(enemy: Char): Boolean {
        if (Level.adjacent(pos, enemy.pos)) {
            return super.doAttack(enemy)
        } else {
            val visible = Level.fieldOfView[pos] || Level.fieldOfView[enemy.pos]
            if (visible) {
                (sprite as WarlockSprite?)?.zap(enemy.pos)
            } else {
                zap()
            }
            return !visible
        }
    }
    private fun zap() {
        spend(TIME_TO_ZAP)
        val currentEnemy = enemy ?: return
        if (hit(this, currentEnemy, true)) {
            if (currentEnemy === Dungeon.hero && Random.Int(2) == 0) {
                Buffs.prolong(currentEnemy, Weakness::class.java, Weakness.duration(currentEnemy))
            }
            val dmg = Random.Int(12, 18)
            currentEnemy.damage(dmg, this)
            if (!currentEnemy.isAlive && currentEnemy === Dungeon.hero) {
                Dungeon.fail(Utils.format(ResultDescriptions.MOB, Utils.indefinite(name), Dungeon.depth))
                GLog.n(TXT_SHADOWBOLT_KILLED, name)
            }
        } else {
            currentEnemy.sprite?.showStatus(CharSprite.NEUTRAL, currentEnemy.defenseVerb())
        }
    }
    fun onZapComplete() {
        zap()
        next()
    }
    override fun call() {
        next()
    }
    override fun description(): String {
        return "When dwarves' interests have shifted from engineering to arcane arts, warlocks have come to power in the city. They started with elemental magic, but soon switched to demonology and necromancy."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private const val TIME_TO_ZAP = 1f
        private const val TXT_SHADOWBOLT_KILLED = "%s's shadow bolt killed you..."
        private val RESISTANCES = hashSetOf<Class<*>>(Death::class.java)
    }
}
