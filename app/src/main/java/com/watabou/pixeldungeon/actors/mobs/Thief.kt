package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.rings.RingOfHaggler
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ThiefSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
open class Thief : Mob() {
    var item: Item? = null
    init {
        name = "crazy thief"
        spriteClass = ThiefSprite::class.java
        HT = 20
        HP = HT
        defenseSkill = 12
        EXP = 5
        maxLvl = 10
        loot = RingOfHaggler::class.java
        lootChance = 0.01f
        FLEEING = ThiefFleeing()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ITEM, item)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        item = bundle.get(ITEM) as Item?
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(1, 7)
    }
    override fun attackDelay(): Float {
        return 0.5f
    }
    override fun die(src: Any?) {
        super.die(src)
        item?.let {
            Dungeon.level?.drop(it, pos)?.sprite?.drop()
        }
    }
    override fun attackSkill(target: Char?): Int {
        return 12
    }
    override fun dr(): Int {
        return 3
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (item == null && enemy is Hero && steal(enemy)) {
            state = FLEEING
        }
        return damage
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        if (state === FLEEING) {
            Dungeon.level?.drop(Gold(), pos)?.sprite?.drop()
        }
        return damage
    }
    protected open fun steal(hero: Hero): Boolean {
        hero.belongings.randomUnequipped()?.let {
            GLog.w(TXT_STOLE, this.name, it.name())
            it.detachAll(hero.belongings.backpack)
            this.item = it
            return true
        }
        return false
    }
    override fun description(): String {
        var desc = "Deeper levels of the dungeon have always been a hiding place for all kinds of criminals. Not all of them could keep a clear mind during their extended periods so far from daylight. Long ago, these crazy thieves and bandits have forgotten who they are and why they steal."
        item?.let {
            desc += String.format(TXT_CARRIES, Utils.capitalize(this.name), it.name())
        }
        return desc
    }
    protected inner class ThiefFleeing : Mob.Fleeing() {
        override fun nowhereToRun() {
            if (buff(Terror::class.java) == null) {
                sprite?.showStatus(CharSprite.NEGATIVE, TXT_RAGE)
                state = HUNTING
            } else {
                super.nowhereToRun()
            }
        }
    }
    companion object {
        protected const val TXT_STOLE = "%s stole %s from you!"
        protected const val TXT_CARRIES = "\n\n%s is carrying a _%s_. Stolen obviously."
        private const val ITEM = "item"
    }
}
