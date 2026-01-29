package com.watabou.pixeldungeon.items.weapon.missiles
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random
class CurareDart : MissileWeapon {
    constructor() : this(1)
    constructor(number: Int) : super() {
        quantity = number
    }
    init {
        name = "curare dart"
        image = ItemSpriteSheet.CURARE_DART
        STR = 14
    }
    override fun min(): Int {
        return 1
    }
    override fun max(): Int {
        return 3
    }
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        Buffs.prolong(defender, Paralysis::class.java, DURATION)
        super.proc(attacker, defender, damage)
    }
    override fun desc(): String {
        return "These little evil darts don't do much damage but they can paralyze " +
                "the target leaving it helpless and motionless for some time."
    }
    override fun random(): Item {
        quantity = Random.Int(2, 5)
        return this
    }
    override fun price(): Int {
        return 12 * quantity
    }
    companion object {
        const val DURATION = 3f
    }
}
