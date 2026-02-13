package com.watabou.pixeldungeon.farming

import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class MelonSeed : CropSeed() {
    override val cropType = CropType.MELON

    init {
        name = "melon seed"
        image = ItemSpriteSheet.SEED_MELON
    }

    override fun info(): String =
        "A melon seed. Takes a long time to grow, but yields a bountiful harvest."

    override fun desc(): String = info()
}
