package com.watabou.pixeldungeon.levels.features

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.WallHardness
import com.watabou.utils.Random

object MiningNoise {

    fun emit(cell: Int, terrain: Int) {
        val level = Dungeon.level ?: return
        val radius = WallHardness.noiseRadius(terrain)
        val wakeChance = WallHardness.wakeChance(terrain)
        for (mob in level.mobs) {
            if (Level.distance(cell, mob.pos) <= radius && Random.Float() < wakeChance) {
                mob.beckon(cell)
            }
        }
    }

    fun emitCaveIn(cell: Int) {
        val level = Dungeon.level ?: return
        // Cave-in noise is heard across the entire level — 100% wake chance
        for (mob in level.mobs) {
            mob.beckon(cell)
        }
    }
}
