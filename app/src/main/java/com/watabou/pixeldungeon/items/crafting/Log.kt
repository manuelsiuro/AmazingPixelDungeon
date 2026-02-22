package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class Log : MaterialItem() {
    init {
        name = "log"
        image = ItemSpriteSheet.LOG
    }

    override fun price(): Int = 3

    override fun info(): String =
        "A rough wooden log, hewn from a dungeon tree. Can be processed into planks or used directly in crafting."

    override fun desc(): String = info()
}
