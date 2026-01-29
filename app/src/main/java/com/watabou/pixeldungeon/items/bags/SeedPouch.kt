package com.watabou.pixeldungeon.items.bags
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.plants.Plant
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class SeedPouch : Bag() {
    init {
        name = "seed pouch"
        image = ItemSpriteSheet.POUCH
        size = 8
    }
    override fun grab(item: Item): Boolean {
        return item is Plant.Seed
    }
    override fun price(): Int {
        return 50
    }
    override fun info(): String {
        return "This small velvet pouch allows you to store any number of seeds in it. Very convenient."
    }
}
