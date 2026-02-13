package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.Item
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle

class SmeltingJob : Bundlable {
    var oreClass: String = ""
    var outputClass: String = ""
    var startTurn: Int = 0
    var duration: Int = 3
    var cell: Int = 0

    override fun storeInBundle(bundle: Bundle) {
        bundle.put(ORE_CLASS, oreClass)
        bundle.put(OUTPUT_CLASS, outputClass)
        bundle.put(START_TURN, startTurn)
        bundle.put(DURATION, duration)
        bundle.put(CELL, cell)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        oreClass = bundle.getString(ORE_CLASS)
        outputClass = bundle.getString(OUTPUT_CLASS)
        startTurn = bundle.getInt(START_TURN)
        duration = bundle.getInt(DURATION)
        cell = bundle.getInt(CELL)
    }

    companion object {
        private const val ORE_CLASS = "oreClass"
        private const val OUTPUT_CLASS = "outputClass"
        private const val START_TURN = "startTurn"
        private const val DURATION = "duration"
        private const val CELL = "cell"
    }
}
