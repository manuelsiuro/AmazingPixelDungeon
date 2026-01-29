package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Amok
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles
import com.watabou.pixeldungeon.sprites.MonkSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.*
open class Monk : Mob() {
    init {
        name = "dwarf monk"
        spriteClass = MonkSprite::class.java
        HT = 70
        HP = HT
        defenseSkill = 30
        EXP = 11
        maxLvl = 21
        loot = Food()
        lootChance = 0.083f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 16)
    }
    override fun attackSkill(target: Char?): Int {
        return 30
    }
    override fun attackDelay(): Float {
        return 0.5f
    }
    override fun dr(): Int {
        return 2
    }
    override fun defenseVerb(): String {
        return "parried"
    }
    override fun die(src: Any?) {
        Imp.Quest.process(this)
        super.die(src)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        val hero = Dungeon.hero
        if (Random.Int(6) == 0 && enemy === hero) {
            val weapon = hero.belongings.weapon
            if (weapon != null && weapon !is Knuckles && !weapon.cursed) {
                hero.belongings.weapon = null
                Dungeon.level?.drop(weapon, hero.pos)?.sprite?.drop()
                GLog.w(TXT_DISARM, name, weapon.name())
            }
        }
        return damage
    }
    override fun description(): String {
        return "These monks are fanatics, who devoted themselves to protecting their city's secrets from all aliens. They don't use any armor or weapons, relying solely on the art of hand-to-hand combat."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        const val TXT_DISARM = "%s has knocked the %s from your hands!"
        private val IMMUNITIES = hashSetOf<Class<*>>(Amok::class.java, Terror::class.java)
    }
}
