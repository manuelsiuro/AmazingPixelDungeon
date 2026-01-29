package com.watabou.pixeldungeon.items.keys
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class SkeletonKey : Key() {
    init {
        name = "skeleton key"
        image = ItemSpriteSheet.SKELETON_KEY
    }
    override fun info(): String {
        return "This key looks serious: its head is shaped like a skull. " +
                "Probably it can open some serious door."
    }
}
