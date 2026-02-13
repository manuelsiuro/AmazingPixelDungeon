package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class BlankTome : MaterialItem() {
    init {
        name = "blank tome"
        image = ItemSpriteSheet.BLANK_TOME
        stackable = false
        unique = true
    }

    override fun price(): Int = 50

    override fun info(): String =
        "A tome of pristine pages, bound in leather and ready to absorb magical enchantments. " +
        "When combined with arcane dust at an enchanting table, it can capture an enchantment for later use."

    override fun desc(): String = info()
}
