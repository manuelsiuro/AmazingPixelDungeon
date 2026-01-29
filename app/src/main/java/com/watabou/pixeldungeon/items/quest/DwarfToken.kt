package com.watabou.pixeldungeon.items.quest
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
class DwarfToken : Item() {
    init {
        name = "dwarf token"
        image = ItemSpriteSheet.TOKEN
        stackable = true
        unique = true
    }
    override val isUpgradable: Boolean
        get() = false
    override val isIdentified: Boolean
        get() = true
    override fun info(): String {
        return "Many dwarves and some of their larger creations carry these small pieces of metal of unknown purpose. " +
                "Maybe they are jewelry or maybe some kind of ID. Dwarves are strange folk."
    }
    override fun price(): Int {
        return quantity * 100
    }
}
