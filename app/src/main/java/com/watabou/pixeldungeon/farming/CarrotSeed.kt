package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class CarrotSeed : CropSeed() {
    override val cropType = CropType.CARROT

    init {
        name = "carrot seed"
        image = ItemSpriteSheet.SEED_CARROT
    }

    override fun info(): String =
        "A tiny carrot seed. Plant it on tilled soil for a nutritious harvest."

    override fun desc(): String = info()
}
