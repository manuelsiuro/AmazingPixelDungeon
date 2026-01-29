package com.watabou.pixeldungeon.items.scrolls
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.effects.Enchanting
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag
class ScrollOfEnchantment : InventoryScroll() {
    init {
        name = "Scroll of Enchantment"
        inventoryTitle = "Select an enchantable item"
        mode = WndBag.Mode.ENCHANTABLE
    }
    override fun onItemSelected(item: Item) {
        val hero = Dungeon.hero ?: return
        val user = curUser ?: return
        ScrollOfRemoveCurse.uncurse(hero, item)
        if (item is Weapon) {
            item.enchant()
        } else {
            (item as Armor).inscribe()
        }
        item.fix()
        user.sprite?.emitter()?.start(Speck.factory(Speck.LIGHT), 0.1f, 5)
        Enchanting.show(user, item)
        GLog.w(TXT_GLOWS, item.name())
    }
    override fun desc(): String {
        return "This scroll is able to imbue a weapon or an armor " +
                "with a random enchantment, granting it a special power."
    }
    companion object {
        private const val TXT_GLOWS = "your %s glows in the dark"
    }
}
