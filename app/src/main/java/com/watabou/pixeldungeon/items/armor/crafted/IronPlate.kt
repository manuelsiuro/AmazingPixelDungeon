package com.watabou.pixeldungeon.items.armor.crafted

import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet

class IronPlate : Armor(3) {
    init {
        name = "iron plate"
        image = ItemSpriteSheet.CRAFTED_IRON_PLATE
    }

    override fun desc(): String =
        "Heavy iron plates hammered into shape and riveted together. Excellent protection, though it slows you down."

    override val isIdentified: Boolean
        get() = true
}
