package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.actors.buffs.Poison
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.enchantments.Death
import com.watabou.pixeldungeon.items.weapon.enchantments.Leech
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.sprites.StatueSprite
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
class Statue : Mob {
    companion object {
        private const val WEAPON = "weapon"
        private val RESISTANCES = hashSetOf<Class<*>>(
            ToxicGas::class.java,
            Poison::class.java,
            Death::class.java,
            ScrollOfPsionicBlast::class.java
        )
        private val IMMUNITIES = hashSetOf<Class<*>>(Leech::class.java)
    }
    private var weapon: Weapon? = null
    init {
        name = "animated statue"
        spriteClass = StatueSprite::class.java
        EXP = 0
        state = PASSIVE
    }
    constructor() : super() {
        var w: Weapon
        do {
            w = Generator.random(Generator.Category.WEAPON) as Weapon
        } while (w !is MeleeWeapon || w.level() < 0)
        weapon = w
        w.identify()
        w.enchant()
        HT = 15 + Dungeon.depth * 5
        HP = HT
        defenseSkill = 4 + Dungeon.depth
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(WEAPON, weapon)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        weapon = bundle.get(WEAPON) as Weapon?
    }
    override fun act(): Boolean {
        if (Dungeon.visible[pos]) {
            Journal.add(Journal.Feature.STATUE)
        }
        return super.act()
    }
    override fun damageRoll(): Int {
        val w = weapon ?: return 0
        return Random.NormalIntRange(w.min(), w.max())
    }
    override fun attackSkill(target: Char?): Int {
        val w = weapon ?: return 0
        return ((9 + Dungeon.depth) * w.ACU).toInt()
    }
    override fun attackDelay(): Float {
        return weapon?.DLY ?: 1f
    }
    override fun dr(): Int {
        return Dungeon.depth
    }
    override fun damage(dmg: Int, src: Any?) {
        if (state == PASSIVE) {
            state = HUNTING
        }
        super.damage(dmg, src)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        weapon?.proc(this, enemy, damage)
        return damage
    }
    override fun beckon(cell: Int) {
        // Do nothing
    }
    override fun die(src: Any?) {
        weapon?.let { Dungeon.level?.drop(it, pos)?.sprite?.drop() }
        super.die(src)
    }
    override fun destroy() {
        Journal.remove(Journal.Feature.STATUE)
        super.destroy()
    }
    override fun reset(): Boolean {
        state = PASSIVE
        return true
    }
    override fun description(): String {
        return "You would think that it's just another ugly statue of this dungeon, but its red glowing eyes give itself away. While the statue itself is made of stone, the _" + (weapon?.name() ?: "weapon") + "_, it's wielding, looks real."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
}
