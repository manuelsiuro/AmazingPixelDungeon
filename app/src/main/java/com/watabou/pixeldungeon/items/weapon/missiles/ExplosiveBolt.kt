package com.watabou.pixeldungeon.items.weapon.missiles

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.BlastParticle
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class ExplosiveBolt : MissileWeapon {

    constructor() : this(1)

    constructor(number: Int) : super() {
        quantity = number
    }

    init {
        name = "explosive bolt"
        image = ItemSpriteSheet.EXPLOSIVE_BOLT
        STR = 14
    }

    override fun min(): Int {
        return 3
    }

    override fun max(): Int {
        return 12
    }

    override fun proc(attacker: Char, defender: Char, damage: Int) {
        super.proc(attacker, defender, damage)

        // Splash damage to adjacent cells
        val splashDamage = damage / 2
        if (splashDamage > 0) {
            for (n in Level.NEIGHBOURS4) {
                val c = defender.pos + n
                if (c >= 0 && c < Level.LENGTH) {
                    if (Dungeon.visible[c]) {
                        CellEmitter.get(c).burst(BlastParticle.FACTORY, 4)
                    }
                    val ch = Actor.findChar(c)
                    if (ch != null && ch !== attacker) {
                        ch.damage(splashDamage, this)
                    }
                }
            }
        }
    }

    override fun desc(): String {
        return "A heavy crossbow bolt tipped with an alchemical charge. On impact, it detonates " +
                "with enough force to damage nearby enemies."
    }

    override fun random(): Item {
        quantity = Random.Int(3, 8)
        return this
    }

    override fun price(): Int {
        return 18 * quantity
    }
}
