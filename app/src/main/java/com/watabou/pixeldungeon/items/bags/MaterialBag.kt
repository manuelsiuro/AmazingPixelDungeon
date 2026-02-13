package com.watabou.pixeldungeon.items.bags

import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.MaterialItem
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class MaterialBag : Bag() {
    init {
        name = "material bag"
        image = ItemSpriteSheet.MATERIAL_BAG
        size = 16
    }

    override fun grab(item: Item): Boolean {
        return item is MaterialItem
    }

    override fun price(): Int = 100

    override fun info(): String =
        "This sturdy bag is designed to hold crafting materials. It " +
            "keeps your raw resources organized and easy to access."
}
