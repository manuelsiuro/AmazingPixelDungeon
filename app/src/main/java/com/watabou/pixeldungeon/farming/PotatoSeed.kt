package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class PotatoSeed : CropSeed() {
    override val cropType = CropType.POTATO

    init {
        name = "potato seed"
        image = ItemSpriteSheet.SEED_POTATO
    }

    override fun info(): String =
        "A sprouting potato eye. Plant it on tilled soil to grow potatoes."

    override fun desc(): String = info()
}
