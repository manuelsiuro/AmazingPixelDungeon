package com.watabou.pixeldungeon.items

import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Cripple
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Random

class ThrowingNet : Item() {

    init {
        name = "throwing net"
        image = ItemSpriteSheet.THROWING_NET
        defaultAction = AC_THROW
        stackable = true
    }

    override fun onThrow(cell: Int) {
        if (Level.pit[cell]) {
            super.onThrow(cell)
        } else {
            val ch = Actor.findChar(cell)
            if (ch != null) {
                Buffs.prolong(ch, Roots::class.java, 5f)
                Buffs.prolong(ch, Cripple::class.java, 3f)
            }
        }
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun random(): Item {
        quantity = Random.IntRange(1, 3)
        return this
    }

    override fun price(): Int {
        return 12 * quantity
    }

    override fun info(): String {
        return "A weighted net designed to entangle enemies. When thrown accurately, it roots the target in place " +
                "and cripples their movement even after they break free."
    }
}
