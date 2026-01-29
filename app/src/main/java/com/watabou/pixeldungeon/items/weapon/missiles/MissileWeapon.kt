package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.windows.WndOptions
abstract class MissileWeapon : Weapon() {
    init {
        stackable = true
        levelKnown = true
        defaultAction = AC_THROW
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        if (hero.heroClass != HeroClass.HUNTRESS && hero.heroClass != HeroClass.ROGUE) {
            actions.remove(AC_EQUIP)
            actions.remove(AC_UNEQUIP)
        }
        return actions
    }
    override fun onThrow(cell: Int) {
        val enemy = Actor.findChar(cell)
        val user = curUser
        if (enemy == null || enemy === user) {
            super.onThrow(cell)
        } else {
            if (user == null || !user.shoot(enemy, this)) {
                miss(cell)
            }
        }
    }
    protected open fun miss(cell: Int) {
        super.onThrow(cell)
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        val hero = attacker as Hero
        if (hero.rangedWeapon == null && stackable) {
            if (quantity == 1) {
                doUnequip(hero, false, false)
            } else {
                detach(hero.belongings.backpack)
            }
        }
    }
    override fun doEquip(hero: Hero): Boolean {
        GameScene.show(
            object : WndOptions(TXT_MISSILES, TXT_R_U_SURE, TXT_YES, TXT_NO) {
                override fun onSelect(index: Int) {
                    if (index == 0) {
                        super@MissileWeapon.doEquip(hero)
                    }
                }
            }
        )
        return false
    }
    override fun random(): Item {
        return this
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        val info = StringBuilder(desc())
        val min = min()
        val max = max()
        info.append("\n\nAverage damage of this weapon equals to ${min + (max - min) / 2} points per hit. ")
        val hero = Dungeon.hero
        if (hero != null && hero.belongings.backpack.items.contains(this)) {
            if (STR > hero.STR()) {
                info.append(
                    "Because of your inadequate strength the accuracy and speed " +
                            "of your attack with this $name is decreased."
                )
            }
            if (STR < hero.STR()) {
                info.append(
                    "Because of your excess strength the damage " +
                            "of your attack with this $name is increased."
                )
            }
        }
        if (hero != null && isEquipped(hero)) {
            info.append("\n\nYou hold the $name at the ready.")
        }
        return info.toString()
    }
    companion object {
        private const val TXT_MISSILES = "Missile weapon"
        private const val TXT_YES = "Yes, I know what I'm doing"
        private const val TXT_NO = "No, I changed my mind"
        private const val TXT_R_U_SURE = "Do you really want to equip it as a melee weapon?"
    }
}
