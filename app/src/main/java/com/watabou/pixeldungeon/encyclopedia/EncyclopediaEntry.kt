package com.watabou.pixeldungeon.encyclopedia

data class EncyclopediaEntry(
    val id: String,
    val name: String,
    val category: EncyclopediaCategory,
    val iconImage: Int,
    val iconType: IconType,
    val description: String,
    val stats: Map<String, String> = emptyMap(),
    val subcategory: String = "",
    val spriteTexture: String = "",
    val spriteWidth: Int = 16,
    val spriteHeight: Int = 15
)

enum class IconType { ITEM, MOB, BUFF, CUSTOM }
