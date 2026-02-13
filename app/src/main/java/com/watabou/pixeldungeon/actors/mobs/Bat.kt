package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.crafting.Leather
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech
import com.watabou.pixeldungeon.sprites.BatSprite
import com.watabou.utils.Random
import java.util.*
import kotlin.math.min
class Bat : Mob() {
    init {
        name = "vampire bat"
        spriteClass = BatSprite::class.java
        HT = 30
        HP = HT
        defenseSkill = 15
        baseSpeed = 2f
        EXP = 7
        maxLvl = 15
        flying = true
        loot = PotionOfHealing()
        lootChance = 0.125f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(6, 12)
    }
    override fun attackSkill(target: Char?): Int {
        return 16
    }
    override fun dr(): Int {
        return 4
    }
    override fun defenseVerb(): String {
        return "evaded"
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        val reg = min(damage, HT - HP)
        if (reg > 0) {
            HP += reg
            sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
        }
        return damage
    }
    override fun dropLoot() {
        super.dropLoot()
        if (Random.Float() < 0.3f) {
            Dungeon.level?.drop(Leather(), pos)?.sprite?.drop()
        }
    }
    override fun description(): String {
        return "These brisk and tenacious inhabitants of cave domes may defeat much larger opponents by replenishing their health with each successful attack."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    companion object {
        private val RESISTANCES = hashSetOf<Class<*>>(Leech::class.java)
    }
}
