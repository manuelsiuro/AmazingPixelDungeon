package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class DiamondShard : MaterialItem() {
    init {
        name = "diamond shard"
        image = ItemSpriteSheet.DIAMOND_SHARD
    }

    override fun price(): Int = 25

    override fun info(): String =
        "A brilliant shard of diamond, impossibly hard and razor-sharp. The rarest crafting material in the dungeon."

    override fun desc(): String = info()
}
