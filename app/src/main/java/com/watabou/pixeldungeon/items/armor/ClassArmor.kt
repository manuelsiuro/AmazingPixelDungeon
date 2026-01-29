package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Bundle
import java.util.ArrayList
abstract class ClassArmor : Armor(6) {
    private var DR: Int = 0
    init {
        levelKnown = true
        cursedKnown = true
        defaultAction = special()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ARMOR_STR, STR)
        bundle.put(ARMOR_DR, DR)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        STR = bundle.getInt(ARMOR_STR)
        DR = bundle.getInt(ARMOR_DR)
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (hero.HP >= 3 && isEquipped(hero)) {
            actions.add(special())
        }
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == special()) {
            if (hero.HP < 3) {
                GLog.w(TXT_LOW_HEALTH)
            } else if (!isEquipped(hero)) {
                GLog.w(TXT_NOT_EQUIPPED)
            } else {
                curUser = hero
                doSpecial()
            }
        } else {
            super.execute(hero, action)
        }
    }
    abstract fun special(): String
    abstract fun doSpecial()
    override fun DR(): Int {
        return DR
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun price(): Int {
        return 0
    }
    override fun desc(): String {
        return "The thing looks awesome!"
    }
    companion object {
        private const val TXT_LOW_HEALTH = "Your health is too low!"
        private const val TXT_NOT_EQUIPPED = "You need to be wearing this armor to use its special power!"
        private const val ARMOR_STR = "STR"
        private const val ARMOR_DR = "DR"
        fun upgrade(owner: Hero, armor: Armor): ClassArmor {
            val classArmor: ClassArmor = when (owner.heroClass) {
                HeroClass.WARRIOR -> WarriorArmor()
                HeroClass.ROGUE -> RogueArmor()
                HeroClass.MAGE -> MageArmor()
                HeroClass.HUNTRESS -> HuntressArmor()
            }
            classArmor.STR = armor.STR
            classArmor.DR = armor.DR()
            classArmor.inscribe(armor.glyph)
            return classArmor
        }
    }
}
