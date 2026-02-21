package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class ArcaneOre : MaterialItem() {
    init {
        name = "arcane ore"
        image = ItemSpriteSheet.ARCANE_ORE
    }

    override fun price(): Int = 40

    override fun info(): String =
        "A chunk of ore infused with arcane energy, mined from deep dungeon walls. Can be smelted into arcane dust."

    override fun desc(): String = info()
}
