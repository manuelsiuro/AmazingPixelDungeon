package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronPickaxe : CraftedPickaxe(3, 1f, 1f) {

    override val pickaxeTier = PickaxeTier.IRON

    init {
        name = "iron pickaxe"
        image = ItemSpriteSheet.IRON_PICKAXE
        STR = 14
    }

    override fun min0(): Int = 5

    override fun max0(): Int = 15

    override fun maxDurability(lvl: Int): Int = 120

    override fun info(): String =
        "A reliable iron pickaxe. Its hardened head can break through granite walls " +
                "and diamond ore veins with ease."

    override fun desc(): String = info()

    override fun price(): Int = 60
}
