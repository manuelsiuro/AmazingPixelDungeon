package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class ArcaneDust : MaterialItem() {
    init {
        name = "arcane dust"
        image = ItemSpriteSheet.ARCANE_DUST
    }

    override fun price(): Int = 10

    override fun info(): String =
        "Shimmering particles of raw magical energy, ground from enchanted artifacts. " +
        "A key ingredient in the art of enchantment."

    override fun desc(): String = info()
}
