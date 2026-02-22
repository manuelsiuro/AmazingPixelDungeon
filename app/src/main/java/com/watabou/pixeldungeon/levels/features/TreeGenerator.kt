package com.watabou.pixeldungeon.levels.features

import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.Terrain
import com.watabou.utils.Random

object TreeGenerator {

    data class TreeConfig(
        val terrain: Int,
        val minClusters: Int,
        val maxClusters: Int,
        val minSize: Int,
        val maxSize: Int
    )

    fun generateTrees(level: Level, depth: Int) {
        val configs = getTreeConfigs(depth)
        for (config in configs) {
            val clusterCount = Random.IntRange(config.minClusters, config.maxClusters)
            for (c in 0 until clusterCount) {
                placeCluster(level, config)
            }
        }
    }

    fun getTreeConfigs(depth: Int): List<TreeConfig> {
        return when {
            // Sewers (1-5): 1-2 small oak/willow clusters near water
            depth in 1..4 -> listOf(
                TreeConfig(Terrain.TREE_OAK, 1, 2, 2, 3),
                TreeConfig(Terrain.TREE_WILLOW, 0, 1, 2, 3)
            )
            depth == 5 -> emptyList() // Boss level
            // Prison (6-10): 0-1 small birch clusters
            depth in 6..9 -> listOf(
                TreeConfig(Terrain.TREE_BIRCH, 0, 1, 2, 3)
            )
            depth == 10 -> emptyList() // Boss level
            // Caves (11-15): 1-2 pine clusters
            depth in 11..14 -> listOf(
                TreeConfig(Terrain.TREE_PINE, 1, 2, 2, 4)
            )
            depth == 15 -> emptyList() // Boss level
            // City (16-20): 0-1 ornamental maple
            depth in 16..19 -> listOf(
                TreeConfig(Terrain.TREE_MAPLE, 0, 1, 1, 2)
            )
            // Halls (21-25) and beyond: No trees
            else -> emptyList()
        }
    }

    private fun placeCluster(level: Level, config: TreeConfig) {
        val size = Random.IntRange(config.minSize, config.maxSize)

        // Try to find a valid starting position
        var attempts = 0
        while (attempts < 50) {
            attempts++
            val startCell = Random.Int(Level.LENGTH)

            // Only place on GRASS or EMPTY tiles, not in rooms
            if (level.map[startCell] != Terrain.GRASS && level.map[startCell] != Terrain.EMPTY) {
                continue
            }

            // Don't place on edges
            val x = startCell % Level.WIDTH
            val y = startCell / Level.WIDTH
            if (x < 2 || x >= Level.WIDTH - 2 || y < 2 || y >= Level.HEIGHT - 2) {
                continue
            }

            // Place the cluster using flood fill from the starting cell
            val placed = ArrayList<Int>()
            val candidates = ArrayList<Int>()
            candidates.add(startCell)

            while (placed.size < size && candidates.isNotEmpty()) {
                val idx = Random.Int(candidates.size)
                val cell = candidates[idx]
                candidates.removeAt(idx)

                if (placed.contains(cell)) continue
                if (level.map[cell] != Terrain.GRASS && level.map[cell] != Terrain.EMPTY) continue

                // Check it's not adjacent to entrance/exit
                if (cell == level.entrance || cell == level.exit) continue

                // Place the tree
                level.map[cell] = config.terrain
                placed.add(cell)

                // Add adjacent cells as candidates
                for (offset in Level.NEIGHBOURS4) {
                    val neighbor = cell + offset
                    if (neighbor >= 0 && neighbor < Level.LENGTH && !placed.contains(neighbor)) {
                        candidates.add(neighbor)
                    }
                }
            }

            if (placed.isNotEmpty()) break // Successfully placed cluster
        }
    }
}
