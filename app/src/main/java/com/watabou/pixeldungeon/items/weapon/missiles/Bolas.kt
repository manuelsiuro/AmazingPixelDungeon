package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Slow
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class Bolas : MissileWeapon {

    constructor() : this(1)

    constructor(number: Int) : super() {
        quantity = number
    }

    init {
        name = "bolas"
        image = ItemSpriteSheet.BOLAS
        STR = 12
    }

    override fun min(): Int {
        return 2
    }

    override fun max(): Int {
        return 8
    }

    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)
        Buffs.prolong(defender, Slow::class.java, 5f)
    }

    override fun desc(): String {
        return "Two heavy stones connected by a strong cord. When thrown, they wrap around " +
                "the target's legs, slowing their movement."
    }

    override fun random(): Item {
        quantity = Random.Int(3, 8)
        return this
    }

    override fun price(): Int {
        return 12 * quantity
    }
}
