package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Amok
import com.watabou.pixeldungeon.actors.buffs.Sleep
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.mobs.npcs.Imp
import com.watabou.pixeldungeon.items.scrolls.ScrollOfPsionicBlast
import com.watabou.pixeldungeon.sprites.GolemSprite
import com.watabou.utils.Random
import java.util.*
class Golem : Mob() {
    init {
        name = "golem"
        spriteClass = GolemSprite::class.java
        HT = 85
        HP = HT
        defenseSkill = 18
        EXP = 12
        maxLvl = 22
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(20, 40)
    }
    override fun attackSkill(target: Char?): Int {
        return 28
    }
    override fun attackDelay(): Float {
        return 1.5f
    }
    override fun dr(): Int {
        return 12
    }
    override fun defenseVerb(): String {
        return "blocked"
    }
    override fun die(src: Any?) {
        Imp.Quest.process(this)
        super.die(src)
    }
    override fun description(): String {
        return "The Dwarves tried to combine their knowledge of mechanisms with their newfound power of elemental binding. They used spirits of earth as the \"soul\" for the mechanical bodies of golems, which were believed to be most controllable of all. Despite this, the tiniest mistake in the ritual could cause an outbreak."
    }
    override fun resistances(): HashSet<Class<*>> {
        return RESISTANCES
    }
    override fun immunities(): HashSet<Class<*>> {
        return IMMUNITIES
    }
    companion object {
        private val RESISTANCES = hashSetOf<Class<*>>(ScrollOfPsionicBlast::class.java)
        private val IMMUNITIES = hashSetOf<Class<*>>(Amok::class.java, Terror::class.java, Sleep::class.java)
    }
}
