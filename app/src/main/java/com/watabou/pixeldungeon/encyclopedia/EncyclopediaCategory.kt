package com.watabou.pixeldungeon.encyclopedia

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

enum class EncyclopediaCategory(val displayName: String, val iconImage: Int) {
    WEAPONS("Weapons", ItemSpriteSheet.SWORD),
    ARMOR("Armor", ItemSpriteSheet.ARMOR_SCALE),
    POTIONS("Potions", ItemSpriteSheet.POTION_TURQUOISE),
    SCROLLS("Scrolls", ItemSpriteSheet.SCROLL_KAUNAN),
    WANDS("Wands", ItemSpriteSheet.WAND_MAGIC_MISSILE),
    RINGS("Rings", ItemSpriteSheet.RING_DIAMOND),
    FOOD("Food", ItemSpriteSheet.PASTY),
    CRAFTING("Crafting", ItemSpriteSheet.IRON_INGOT),
    FARMING("Farming", ItemSpriteSheet.HOE),
    MONSTERS("Monsters", ItemSpriteSheet.SKULL),
    BUFFS("Buffs", ItemSpriteSheet.TORCH),
    MECHANICS("Mechanics", ItemSpriteSheet.MASTERY)
}
