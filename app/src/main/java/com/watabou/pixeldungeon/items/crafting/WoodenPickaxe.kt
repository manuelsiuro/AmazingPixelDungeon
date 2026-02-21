package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class WoodenPickaxe : CraftedPickaxe(1, 1f, 1f) {

    override val pickaxeTier = PickaxeTier.WOOD

    init {
        name = "wooden pickaxe"
        image = ItemSpriteSheet.WOODEN_PICKAXE
        STR = 10
    }

    override fun min0(): Int = 2

    override fun max0(): Int = 8

    override fun maxDurability(lvl: Int): Int = 30

    override fun info(): String =
        "A crude pickaxe with a wooden head. Only suitable for mining soft dirt walls, " +
                "but it's better than bare hands."

    override fun desc(): String = info()

    override fun price(): Int = 15
}
