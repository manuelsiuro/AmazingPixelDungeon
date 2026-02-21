package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class StonePickaxe : CraftedPickaxe(2, 1f, 1f) {

    override val pickaxeTier = PickaxeTier.STONE

    init {
        name = "stone pickaxe"
        image = ItemSpriteSheet.STONE_PICKAXE
        STR = 12
    }

    override fun min0(): Int = 3

    override fun max0(): Int = 12

    override fun maxDurability(lvl: Int): Int = 60

    override fun info(): String =
        "A sturdy pickaxe with a stone head. Capable of mining through natural stone walls " +
                "and common ore veins."

    override fun desc(): String = info()

    override fun price(): Int = 30
}
