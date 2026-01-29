package com.watabou.pixeldungeon.items.quest
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class CorpseDust : Item() {
    init {
        name = "corpse dust"
        image = ItemSpriteSheet.DUST
        cursed = true
        cursedKnown = true
        unique = true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "The ball of corpse dust doesn't differ outwardly from a regular dust ball. However, " +
                "you know somehow that it's better to get rid of it as soon as possible."
    }
}
