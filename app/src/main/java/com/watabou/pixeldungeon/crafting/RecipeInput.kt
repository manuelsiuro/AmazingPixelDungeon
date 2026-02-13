package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.Item

data class RecipeInput(
    val itemClass: Class<out Item>,
    val quantity: Int,
    val tag: MaterialTag? = null
)
