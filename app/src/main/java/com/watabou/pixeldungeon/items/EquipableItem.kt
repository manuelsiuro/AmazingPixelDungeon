package com.watabou.pixeldungeon.items
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.crafting.CraftingManager
import com.watabou.pixeldungeon.effects.particles.ShadowParticle
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.utils.GLog
abstract class EquipableItem : Item() {
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (isAdjacentToCraftingTable(hero)) {
            actions.add(AC_SALVAGE)
        }
        return actions
    }
    override fun execute(hero: Hero, action: String) {
        if (action == AC_EQUIP) {
            doEquip(hero)
        } else if (action == AC_UNEQUIP) {
            doUnequip(hero, true)
        } else if (action == AC_SALVAGE) {
            doSalvage(hero)
        } else {
            super.execute(hero, action)
        }
    }
    private fun doSalvage(hero: Hero) {
        if (isEquipped(hero) && !doUnequip(hero, false, false)) {
            return
        }
        val materials = CraftingManager.salvage(this)
        if (materials.isEmpty()) {
            GLog.w("You can't salvage anything useful from this.")
            return
        }
        detachAll(hero.belongings.backpack)
        for (mat in materials) {
            if (!mat.collect(hero.belongings.backpack)) {
                Dungeon.level?.drop(mat, hero.pos)?.sprite?.drop()
            }
        }
        hero.spendAndNext(2f)
        GLog.p("You salvage materials from the %s.", name())
        Sample.play(Assets.SND_EVOKE)
    }
    private fun isAdjacentToCraftingTable(hero: Hero): Boolean {
        val level = Dungeon.level ?: return false
        for (offset in Level.NEIGHBOURS8) {
            val cell = hero.pos + offset
            if (cell >= 0 && cell < Level.LENGTH && level.map[cell] == Terrain.CRAFTING_TABLE) {
                return true
            }
        }
        return false
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
        const val AC_SALVAGE = "SALVAGE"
        fun equipCursed(hero: Hero) {
            hero.sprite?.emitter()?.burst(ShadowParticle.CURSE, 6)
            Sample.play(Assets.SND_CURSED)
        }
    }
}
