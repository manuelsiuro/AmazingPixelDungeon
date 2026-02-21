package com.watabou.pixeldungeon.levels.features

import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.pixeldungeon.levels.WallHardness
import com.watabou.utils.Random

object OreGenerator {

    private data class VeinConfig(
        val oreType: Int,
        val minVeins: Int,
        val maxVeins: Int,
        val minSize: Int,
        val maxSize: Int
    )

    fun generate(level: Level, depth: Int) {
        val mineableWalls = mutableListOf<Int>()
        for (i in 0 until Level.LENGTH) {
            if (WallHardness.isMineableWall(level.map[i])) {
                mineableWalls.add(i)
            }
        }

        if (mineableWalls.isEmpty()) return

        val configs = getVeinConfigs(depth)

        for (config in configs) {
            val numVeins = Random.IntRange(config.minVeins, config.maxVeins)
            for (v in 0 until numVeins) {
                if (mineableWalls.isEmpty()) return

                val veinSize = Random.IntRange(config.minSize, config.maxSize)
                val seedIndex = Random.Int(mineableWalls.size)
                val seed = mineableWalls[seedIndex]

                // Flood-fill from seed
                val vein = mutableListOf(seed)
                val frontier = mutableListOf(seed)

                while (vein.size < veinSize && frontier.isNotEmpty()) {
                    val current = frontier.removeAt(Random.Int(frontier.size))
                    for (n in Level.NEIGHBOURS4) {
                        val adj = current + n
                        if (adj in 0 until Level.LENGTH &&
                            WallHardness.isMineableWall(level.map[adj]) &&
                            adj !in vein
                        ) {
                            vein.add(adj)
                            frontier.add(adj)
                            if (vein.size >= veinSize) break
                        }
                    }
                }

                // Convert vein cells to ore type and remove from available walls
                for (cell in vein) {
                    level.map[cell] = config.oreType
                    mineableWalls.remove(cell)
                }
            }
        }
    }

    private fun getVeinConfigs(depth: Int): List<VeinConfig> {
        val configs = mutableListOf<VeinConfig>()

        when (depth) {
            in 1..4 -> {
                configs.add(VeinConfig(Terrain.ORE_WALL_IRON, 1, 2, 1, 2))
            }
            in 6..9 -> {
                configs.add(VeinConfig(Terrain.ORE_WALL_IRON, 2, 3, 2, 3))
                configs.add(VeinConfig(Terrain.ORE_WALL_GOLD, 0, 1, 1, 2))
            }
            in 11..14 -> {
                configs.add(VeinConfig(Terrain.ORE_WALL_IRON, 3, 4, 2, 4))
                configs.add(VeinConfig(Terrain.ORE_WALL_GOLD, 1, 2, 2, 3))
                configs.add(VeinConfig(Terrain.ORE_WALL_DIAMOND, 0, 1, 1, 2))
            }
            in 16..19 -> {
                configs.add(VeinConfig(Terrain.ORE_WALL_GOLD, 2, 3, 2, 4))
                configs.add(VeinConfig(Terrain.ORE_WALL_DIAMOND, 1, 2, 1, 3))
            }
            in 22..24 -> {
                configs.add(VeinConfig(Terrain.ORE_WALL_DIAMOND, 2, 3, 2, 3))
                configs.add(VeinConfig(Terrain.ORE_WALL_ARCANE, 0, 1, 1, 2))
            }
        }

        return configs
    }
}
