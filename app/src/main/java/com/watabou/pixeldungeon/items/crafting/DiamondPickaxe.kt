package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.levels.PickaxeTier
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class DiamondPickaxe : CraftedPickaxe(4, 1f, 1f) {

    override val pickaxeTier = PickaxeTier.DIAMOND

    init {
        name = "diamond pickaxe"
        image = ItemSpriteSheet.DIAMOND_PICKAXE
        STR = 16
    }

    override fun min0(): Int = 7

    override fun max0(): Int = 20

    override fun maxDurability(lvl: Int): Int = 240

    override fun info(): String =
        "The finest pickaxe money can buy, tipped with diamond. It can mine through " +
                "even obsidian and arcane ore walls with remarkable speed."

    override fun desc(): String = info()

    override fun price(): Int = 120
}
