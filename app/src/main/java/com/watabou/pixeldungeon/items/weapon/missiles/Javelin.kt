package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class Javelin : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "javelin"
        image = ItemSpriteSheet.JAVELIN
        STR = 15
    }
    override fun min(): Int {
        return 2
    }
    override fun max(): Int {
        return 15
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        Buffs.prolong(defender, Cripple::class.java, Cripple.DURATION)
    }
    override fun desc(): String {
        return "This length of metal is weighted to keep the spike " +
                "at its tip foremost as it sails through the air."
    }
    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }
    override fun price(): Int {
        return 15 * quantity
    }
}
