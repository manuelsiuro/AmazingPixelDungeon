package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.Item

data class Recipe(
    val id: String,
    val inputs: List<RecipeInput>,
    val outputClass: Class<out Item>,
    val outputQuantity: Int = 1,
    val station: StationType = StationType.NONE,
    val craftTime: Float = 1f
)
