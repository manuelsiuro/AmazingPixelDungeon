package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class Tamahawk : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "tomahawk"
        image = ItemSpriteSheet.TOMAHAWK
        STR = 17
    }
    override fun min(): Int {
        return 4
    }
    override fun max(): Int {
        return 20
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        Buffs.affect(defender, Bleeding::class.java)?.set(damage)
    }
    override fun desc(): String {
        return "This throwing axe is not that heavy, but it still " +
                "requires significant strength to be used effectively."
    }
    override fun random(): Item {
        quantity = Random.Int(5, 12)
        return this
    }
    override fun price(): Int {
        return 20 * quantity
    }
}
