package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class Dart : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "dart"
        image = ItemSpriteSheet.DART
    }
    override fun min(): Int {
        return 1
    }
    override fun max(): Int {
        return 4
    }
    override fun desc(): String {
        return "These simple metal spikes are weighted to fly true and " +
                "sting their prey with a flick of the wrist."
    }
    override fun random(): Item {
        quantity = Random.Int(5, 15)
        return this
    }
    override fun price(): Int {
        return quantity * 2
    }
}
