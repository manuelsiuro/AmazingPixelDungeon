package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.sprites.BruteSprite
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.*
open class Brute : Mob() {
    private var enraged = false
    init {
        name = "gnoll brute"
        spriteClass = BruteSprite::class.java
        HT = 40
        HP = HT
        defenseSkill = 15
        EXP = 8
        maxLvl = 15
        loot = Gold::class.java
        lootChance = 0.5f
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        enraged = HP < HT / 4
    }
    override fun damageRoll(): Int {
        return if (enraged) {
            Random.NormalIntRange(10, 40)
        } else {
            Random.NormalIntRange(8, 18)
        }
    }
    override fun attackSkill(target: Char?): Int {
        return 20
    }
    override fun dr(): Int {
        return 8
    }
    override fun damage(dmg: Int, src: Any?) {
        super.damage(dmg, src)
        if (isAlive && !enraged && HP < HT / 4) {
            enraged = true
            spend(TICK)
            if (Dungeon.visible[pos]) {
                GLog.w(TXT_ENRAGED, name)
                sprite?.showStatus(CharSprite.NEGATIVE, "enraged")
            }
        }
    }
    override fun description(): String {
        return "Brutes are the largest, strongest and toughest of all gnolls. When severely wounded, they go berserk, inflicting even more damage to their enemies."
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private const val TXT_ENRAGED = "%s becomes enraged!"
        private val IMMUNITIES = hashSetOf<Class<*>>(Terror::class.java)
    }
}
