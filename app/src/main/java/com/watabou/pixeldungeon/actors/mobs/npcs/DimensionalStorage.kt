package com.watabou.pixeldungeon.actors.mobs.npcs

import com.watabou.pixeldungeon.items.Item
import com.watabou.utils.Bundle
import java.util.ArrayList

object DimensionalStorage {

    const val MAX_ITEMS = 8
    private const val ITEMS = "items"

    val items = ArrayList<Item>()

    fun reset() {
        items.clear()
    }

    fun storeInBundle(bundle: Bundle) {
        bundle.put(ITEMS, items)
    }

    fun restoreFromBundle(bundle: Bundle) {
        items.clear()
        for (item in bundle.getCollection(ITEMS)) {
            items.add(item as Item)
        }
    }
}
