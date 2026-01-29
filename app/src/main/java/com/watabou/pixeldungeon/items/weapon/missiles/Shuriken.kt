package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class Shuriken : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "shuriken"
        image = ItemSpriteSheet.SHURIKEN
        STR = 13
        DLY = 0.5f
    }
    override fun min(): Int {
        return 2
    }
    override fun max(): Int {
        return 6
    }
    override fun desc(): String {
        return "Star-shaped pieces of metal with razor-sharp blades do significant damage " +
                "when they hit a target. They can be thrown at very high rate."
    }
    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }
    override fun price(): Int {
        return 15 * quantity
    }
}
