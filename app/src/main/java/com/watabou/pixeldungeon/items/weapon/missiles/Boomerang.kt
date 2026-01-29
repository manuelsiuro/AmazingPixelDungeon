package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.sprites.MissileSprite
class Boomerang : MissileWeapon() {
    init {
        name = "boomerang"
        image = ItemSpriteSheet.BOOMERANG
        STR = 10
        stackable = false
    }
    override fun min(): Int {
        return if (isBroken) 1 else 1 + level()
    }
    override fun max(): Int {
        return if (isBroken) 4 else 4 + 2 * level()
    }
    override val isUpgradable: Boolean
        get() = true
    override fun upgrade(): Item {
        return upgrade(false)
    }
    override fun upgrade(enchant: Boolean): Item {
        super.upgrade(enchant)
        updateQuickslot()
        return this
    }
    override fun maxDurability(lvl: Int): Int {
        return 8 * if (lvl < 16) 16 - lvl else 1
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        if (attacker is Hero && attacker.rangedWeapon === this) {
            circleBack(defender.pos, attacker)
        }
    }
    override fun miss(cell: Int) {
        circleBack(cell, curUser as Hero)
    }
    private fun circleBack(from: Int, owner: Hero) {
        ((owner.sprite?.parent?.recycle(MissileSprite::class.java) as? MissileSprite))?.reset(from, owner.pos, curItem, null)
        if (throwEquiped) {
            owner.belongings.weapon = this
            owner.spend(-KindOfWeapon.TIME_TO_EQUIP)
        } else if (!collect(owner.belongings.backpack)) {
            Dungeon.level?.drop(this, owner.pos)?.sprite?.drop()
        }
    }
    private var throwEquiped: Boolean = false
    override fun cast(user: Hero, dst: Int) {
        throwEquiped = isEquipped(user)
        super.cast(user, dst)
    }
    override fun desc(): String {
        return "Thrown to the enemy this flat curved wooden missile will return to the hands of its thrower."
    }
}
