package com.watabou.pixeldungeon.items.bags
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.keys.Key
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class Keyring : Bag() {
    init {
        name = "key ring"
        image = ItemSpriteSheet.KEYRING
        size = 12
    }
    override fun grab(item: Item): Boolean {
        return item is Key
    }
    override fun price(): Int {
        return 50
    }
    override fun info(): String {
        return "This is a copper key ring, that lets you keep all your keys " +
                "separately from the rest of your belongings."
    }
}
