package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Bark : MaterialItem() {
    init {
        name = "bark"
        image = ItemSpriteSheet.BARK
    }

    override fun price(): Int = 1

    override fun info(): String =
        "Strips of tree bark, peeled from dungeon trees. Useful for crafting rope and other simple materials."

    override fun desc(): String = info()
}
