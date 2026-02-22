package com.watabou.pixeldungeon.levels

enum class TreeHardness(val hp: Int, val minTier: AxeTier) {
    SOFT(2, AxeTier.WOOD),      // Birch, Willow, Fruit
    MEDIUM(4, AxeTier.STONE),   // Oak, Maple
    HARD(6, AxeTier.IRON);      // Pine

    companion object {
        fun forTerrain(terrain: Int): TreeHardness? = when (terrain) {
            Terrain.TREE_OAK -> MEDIUM
            Terrain.TREE_BIRCH -> SOFT
            Terrain.TREE_PINE -> HARD
            Terrain.TREE_MAPLE -> MEDIUM
            Terrain.TREE_WILLOW -> SOFT
            Terrain.TREE_FRUIT -> SOFT
            Terrain.TREE_DAMAGED -> null // already tracked via blockHP
            else -> null
        }

        fun isChoppableTree(terrain: Int): Boolean = Terrain.isChoppable(terrain)

        fun noiseRadius(terrain: Int): Int = when (forTerrain(terrain)) {
            SOFT -> 3
            MEDIUM -> 5
            HARD -> 7
            null -> 4
        }

        fun wakeChance(terrain: Int): Float = when (forTerrain(terrain)) {
            SOFT -> 0.2f
            MEDIUM -> 0.4f
            HARD -> 0.6f
            null -> 0.3f
        }
    }
}
