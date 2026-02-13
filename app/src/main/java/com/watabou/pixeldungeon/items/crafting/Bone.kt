package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Bone : MaterialItem() {
    init {
        name = "bone"
        image = ItemSpriteSheet.BONE
    }

    override fun info(): String =
        "A bone from a fallen skeleton. Can be ground into bonemeal."

    override fun desc(): String = info()

    override fun price(): Int = 2 * quantity
}
