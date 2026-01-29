package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.actors.buffs.Light
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.mechanics.Ballistica
import com.watabou.pixeldungeon.sprites.ScorpioSprite
import com.watabou.utils.Random
import java.util.*
open class Scorpio : Mob() {
    init {
        name = "scorpio"
        spriteClass = ScorpioSprite::class.java
        HT = 95
        HP = HT
        defenseSkill = 24
        viewDistance = Light.DISTANCE
        EXP = 14
        maxLvl = 25
        loot = PotionOfHealing()
        lootChance = 0.125f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(20, 32)
    }
    override fun attackSkill(target: Char?): Int {
        return 36
    }
    override fun dr(): Int {
        return 16
    }
    override fun canAttack(enemy: Char): Boolean {
        return !Level.adjacent(pos, enemy.pos) && Ballistica.cast(pos, enemy.pos, false, true) == enemy.pos
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(2) == 0) {
            Buffs.prolong(enemy, Cripple::class.java, Cripple.DURATION)
        }
        return damage
    }
    override fun getCloser(target: Int): Boolean {
        return if (state === HUNTING) {
            enemySeen && getFurther(target)
        } else {
            super.getCloser(target)
        }
    }
    override fun dropLoot() {
        if (Random.Int(8) == 0) {
            Dungeon.level?.drop(PotionOfHealing(), pos)?.sprite?.drop()
        } else if (Random.Int(6) == 0) {
            Dungeon.level?.drop(MysteryMeat(), pos)?.sprite?.drop()
        }
    }
    override fun description(): String {
        return "These huge arachnid-like demonic creatures avoid close combat by all means, firing crippling serrated spikes from long distances."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private val RESISTANCES = hashSetOf<Class<*>>(Leech::class.java, Poison::class.java)
    }
}
