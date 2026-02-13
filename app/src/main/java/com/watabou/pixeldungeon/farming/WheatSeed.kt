package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WheatSeed : CropSeed() {
    override val cropType = CropType.WHEAT

    init {
        name = "wheat seed"
        image = ItemSpriteSheet.SEED_WHEAT
    }

    override fun info(): String =
        "A small grain seed. Plant it on tilled soil and it will grow into wheat over time."

    override fun desc(): String = info()
}
