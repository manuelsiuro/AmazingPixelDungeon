package com.watabou.pixeldungeon.items.keys
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.utils.Bundle
open class Key : Item {
    var depth: Int
    constructor() : super() {
        stackable = false
        depth = Dungeon.depth
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(DEPTH, depth)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        depth = bundle.getInt(DEPTH)
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun status(): String {
        return "$depth\u007F"
    }
    companion object {
        const val TIME_TO_UNLOCK = 1f
        private const val DEPTH = "depth"
    }
}
