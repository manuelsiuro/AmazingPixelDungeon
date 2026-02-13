package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class EyeOfEnder : MaterialItem() {
    init {
        name = "eye of ender"
        image = ItemSpriteSheet.EYE_OF_ENDER
        stackable = true
    }

    override fun price(): Int = 50

    override fun info(): String =
        "A mysterious eye that gazes into other dimensions. It pulses with an otherworldly energy."

    override fun desc(): String = info()
}
