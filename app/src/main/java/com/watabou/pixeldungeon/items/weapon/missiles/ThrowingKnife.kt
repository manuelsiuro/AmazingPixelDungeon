package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class ThrowingKnife : MissileWeapon {

    constructor() : this(1)

    constructor(number: Int) : super() {
        quantity = number
    }

    init {
        name = "throwing knife"
        image = ItemSpriteSheet.THROWING_KNIFE
        STR = 9
        DLY = 0.5f
    }

    override fun min(): Int {
        return 1
    }

    override fun max(): Int {
        return 4
    }

    override fun desc(): String {
        return "A small, balanced blade designed for rapid throwing. What it lacks in damage, it makes up for in speed."
    }

    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }

    override fun price(): Int {
        return 6 * quantity
    }
}
