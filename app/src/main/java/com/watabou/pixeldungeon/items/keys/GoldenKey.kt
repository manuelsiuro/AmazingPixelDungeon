package com.watabou.pixeldungeon.items.keys
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class GoldenKey : Key() {
    init {
        name = "golden key"
        image = ItemSpriteSheet.GOLDEN_KEY
    }
    override fun info(): String {
        return "The notches on this golden key are tiny and intricate. " +
                "Maybe it can open some chest lock?"
    }
}
