package com.watabou.pixeldungeon.farming

enum class CropType(
    val cropName: String,
    val growthTime: Int,
    val minYield: Int,
    val maxYield: Int
) {
    WHEAT("Wheat", 40, 1, 2),
    CARROT("Carrot", 30, 1, 3),
    POTATO("Potato", 30, 1, 3),
    MELON("Melon", 60, 3, 5)
}
