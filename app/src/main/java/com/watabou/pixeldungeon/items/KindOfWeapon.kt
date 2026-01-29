package com.watabou.pixeldungeon.items
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.ui.QuickSlot
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random
import java.util.ArrayList
abstract class KindOfWeapon : EquipableItem() {
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }
    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.weapon === this
    }
    override fun doEquip(hero: Hero): Boolean {
        detachAll(hero.belongings.backpack)
        val currentWeapon = hero.belongings.weapon
        if (currentWeapon == null || currentWeapon.doUnequip(hero, true)) {
            hero.belongings.weapon = this
            activate(hero)
            QuickSlot.refresh()
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n(TXT_EQUIP_CURSED, name())
            }
            hero.spendAndNext(TIME_TO_EQUIP)
            return true
        } else {
            collect(hero.belongings.backpack)
            return false
        }
    }
    override fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            hero.belongings.weapon = null
            return true
        } else {
            return false
        }
    }
    open fun activate(hero: Hero) {}
    abstract fun min(): Int
    abstract fun max(): Int
    open fun damageRoll(owner: Hero): Int {
        return Random.NormalIntRange(min(), max())
    }
    open fun acuracyFactor(hero: Hero): Float {
        return 1f
    }
    open fun speedFactor(hero: Hero): Float {
        return 1f
    }
    open fun proc(attacker: Char, defender: Char, damage: Int) {}
    companion object {
        private const val TXT_EQUIP_CURSED = "you wince as your grip involuntarily tightens around your %s"
        const val TIME_TO_EQUIP = 1f
    }
}
