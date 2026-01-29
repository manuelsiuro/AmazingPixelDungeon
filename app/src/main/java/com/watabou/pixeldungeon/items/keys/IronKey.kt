package com.watabou.pixeldungeon.items.keys
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.utils.Utils
class IronKey : Key() {
    init {
        name = "iron key"
        image = ItemSpriteSheet.IRON_KEY
    }
    override fun collect(container: Bag?): Boolean {
        val result = super.collect(container)
        if (result && depth == Dungeon.depth) {
            Dungeon.hero?.belongings?.countIronKeys()
        }
        return result
    }
    override fun onDetach() {
        if (depth == Dungeon.depth) {
            Dungeon.hero?.belongings?.countIronKeys()
        }
    }
    override fun toString(): String {
        return Utils.format(TXT_FROM_DEPTH, depth)
    }
    override fun info(): String {
        return "The notches on this ancient iron key are well worn; its leather lanyard " +
                "is battered by age. What door might it open?"
    }
    companion object {
        private const val TXT_FROM_DEPTH = "iron key from depth %d"
        var curDepthQuantity = 0
    }
}
