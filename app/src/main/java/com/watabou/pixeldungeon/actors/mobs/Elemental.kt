package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Frost
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.potions.PotionOfLiquidFlame
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.wands.WandOfFirebolt
import com.watabou.pixeldungeon.items.weapon.enchantments.Fire
import com.watabou.pixeldungeon.sprites.ElementalSprite
import com.watabou.utils.Random
import java.util.*
class Elemental : Mob() {
    init {
        name = "fire elemental"
        spriteClass = ElementalSprite::class.java
        HT = 65
        HP = HT
        defenseSkill = 20
        EXP = 10
        maxLvl = 20
        flying = true
        loot = PotionOfLiquidFlame()
        lootChance = 0.1f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(16, 20)
    }
    override fun attackSkill(target: Char?): Int {
        return 25
    }
    override fun dr(): Int {
        return 5
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(2) == 0) {
            Buffs.affect(enemy, Burning::class.java)?.reignite(enemy)
        }
        return damage
    }
    override fun add(buff: Buff) {
        if (buff is Burning) {
            if (HP < HT) {
                HP++
                sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
            }
        } else {
            if (buff is Frost) {
                damage(Random.NormalIntRange(1, HT * 2 / 3), buff)
            }
            super.add(buff)
        }
    }
    override fun description(): String {
        return "Wandering fire elementals are a byproduct of summoning greater entities. They are too chaotic in their nature to be controlled by even the most powerful demonologist."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val IMMUNITIES = hashSetOf<Class<*>>(
            Burning::class.java,
            Fire::class.java,
            WandOfFirebolt::class.java,
            ScrollOfPsionicBlast::class.java
        )
    }
}
