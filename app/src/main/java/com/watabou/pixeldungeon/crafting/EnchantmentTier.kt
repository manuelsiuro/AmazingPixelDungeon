package com.watabou.pixeldungeon.crafting

enum class EnchantmentTier(
    val dustCost: Int,
    val xpPercent: Float,
    val levelCost: Int
) {
    TIER_1(dustCost = 5, xpPercent = 0f, levelCost = 0),
    TIER_2(dustCost = 15, xpPercent = 0.10f, levelCost = 0),
    TIER_3(dustCost = 40, xpPercent = 0f, levelCost = 1)
}
