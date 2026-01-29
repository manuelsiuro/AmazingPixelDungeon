package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.Web
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.SpinnerSprite
import com.watabou.utils.Random
import java.util.*
class Spinner : Mob() {
    init {
        name = "cave spinner"
        spriteClass = SpinnerSprite::class.java
        HT = 50
        HP = HT
        defenseSkill = 14
        EXP = 9
        maxLvl = 16
        loot = MysteryMeat()
        lootChance = 0.125f
        FLEEING = SpinnerFleeing()
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 16)
    }
    override fun attackSkill(target: Char?): Int {
        return 20
    }
    override fun dr(): Int {
        return 6
    }
    override fun act(): Boolean {
        val result = super.act()
        if (state === FLEEING && buff(Terror::class.java) == null) {
            val currentEnemy = enemy
            if (currentEnemy != null && enemySeen && currentEnemy.buff(Poison::class.java) == null) {
                state = HUNTING
            }
        }
        return result
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(2) == 0) {
            Buffs.affect(enemy, Poison::class.java)?.set(Random.Int(7, 9) * Poison.durationFactor(enemy))
            state = FLEEING
        }
        return damage
    }
    override fun move(step: Int) {
        if (state === FLEEING) {
            GameScene.add(Blob.seed(pos, Random.Int(5, 7), Web::class.java) as Blob)
        }
        super.move(step)
    }
    override fun description(): String {
        return "These greenish furry cave spiders try to avoid direct combat, preferring to wait in the distance while their victim, entangled in the spinner's excreted cobweb, slowly dies from their poisonous bite."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    private inner class SpinnerFleeing : Mob.Fleeing() {
        override fun nowhereToRun() {
            if (buff(Terror::class.java) == null) {
                state = HUNTING
            } else {
                super.nowhereToRun()
            }
        }
    }
    companion object {
        private val RESISTANCES = hashSetOf<Class<*>>(Poison::class.java)
        private val IMMUNITIES = hashSetOf<Class<*>>(Roots::class.java)
    }
}
