package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.utils.GLog
abstract class EquipableItem : Item() {
    override fun execute(hero: Hero, action: String) {
        if (action == AC_EQUIP) {
            doEquip(hero)
        } else if (action == AC_UNEQUIP) {
            doUnequip(hero, true)
        } else {
            super.execute(hero, action)
        }
    }
    override fun doDrop(hero: Hero) {
        if (!isEquipped(hero) || doUnequip(hero, false, false)) {
            super.doDrop(hero)
        }
    }
    override fun cast(user: Hero, dst: Int) {
        if (isEquipped(user)) {
            if (quantity == 1 && !this.doUnequip(user, false, false)) {
                return
            }
        }
        super.cast(user, dst)
    }
    protected open fun time2equip(hero: Hero): Float {
        return 1f
    }
    abstract fun doEquip(hero: Hero): Boolean
    open fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        if (cursed) {
            GLog.w(TXT_UNEQUIP_CURSED, name())
            return false
        }
        if (single) {
            hero.spendAndNext(time2equip(hero))
        } else {
            hero.spend(time2equip(hero))
        }
        if (collect && !collect(hero.belongings.backpack)) {
            Dungeon.level?.drop(this, hero.pos)
        }
        return true
    }
    fun doUnequip(hero: Hero, collect: Boolean): Boolean {
        return doUnequip(hero, collect, true)
    }
    companion object {
        private const val TXT_UNEQUIP_CURSED = "You can't remove cursed %s!"
        const val AC_EQUIP = "EQUIP"
        const val AC_UNEQUIP = "UNEQUIP"
        fun equipCursed(hero: Hero) {
            hero.sprite?.emitter()?.burst(ShadowParticle.CURSE, 6)
            Sample.play(Assets.SND_CURSED)
        }
    }
}
