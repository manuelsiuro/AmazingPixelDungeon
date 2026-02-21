package com.watabou.pixeldungeon.levels

enum class PickaxeTier(val miningDamage: Int, val miningTime: Float, val maxDurability: Int, val strReq: Int, val hungerDivisor: Float) {
    WOOD(1, 3f, 30, 10, 15f),
    STONE(2, 2.5f, 60, 12, 12f),
    IRON(3, 2f, 120, 14, 10f),
    DIAMOND(4, 1.5f, 240, 16, 10f)
}

enum class WallHardness(val hp: Int, val minTier: PickaxeTier) {
    DIRT(1, PickaxeTier.WOOD),
    STONE(3, PickaxeTier.STONE),
    GRANITE(5, PickaxeTier.IRON),
    OBSIDIAN(8, PickaxeTier.DIAMOND);

    companion object {
        fun forTerrain(terrain: Int): WallHardness? = when (terrain) {
            Terrain.DIRT_WALL -> DIRT
            Terrain.STONE_WALL_NATURAL -> STONE
            Terrain.GRANITE_WALL -> GRANITE
            Terrain.OBSIDIAN_WALL -> OBSIDIAN
            Terrain.CRACKED_WALL -> null // already tracked via blockHP
            // Ore walls inherit hardness from their biome
            Terrain.ORE_WALL_IRON -> STONE
            Terrain.ORE_WALL_GOLD -> STONE
            Terrain.ORE_WALL_DIAMOND -> GRANITE
            Terrain.ORE_WALL_ARCANE -> OBSIDIAN
            else -> null
        }

        fun isMineableWall(terrain: Int): Boolean = when (terrain) {
            Terrain.DIRT_WALL, Terrain.STONE_WALL_NATURAL,
            Terrain.GRANITE_WALL, Terrain.OBSIDIAN_WALL,
            Terrain.ORE_WALL_IRON, Terrain.ORE_WALL_GOLD,
            Terrain.ORE_WALL_DIAMOND, Terrain.ORE_WALL_ARCANE,
            Terrain.CRACKED_WALL -> true
            else -> false
        }

        fun noiseRadius(terrain: Int): Int = when (forTerrain(terrain)) {
            DIRT -> 4
            STONE -> 6
            GRANITE -> 8
            OBSIDIAN -> 10
            null -> 6
        }

        fun wakeChance(terrain: Int): Float = when (forTerrain(terrain)) {
            DIRT -> 0.3f
            STONE -> 0.5f
            GRANITE -> 0.7f
            OBSIDIAN -> 0.9f
            null -> 0.5f
        }
    }
}
