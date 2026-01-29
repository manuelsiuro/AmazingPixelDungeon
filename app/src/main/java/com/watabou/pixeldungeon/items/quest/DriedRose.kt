package com.watabou.pixeldungeon.items.quest
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class DriedRose : Item() {
    init {
        name = "dried rose"
        image = ItemSpriteSheet.ROSE
        unique = true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "The rose has dried long ago, but it has kept all its petals somehow."
    }
}
