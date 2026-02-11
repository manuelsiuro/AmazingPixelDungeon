package com.watabou.pixeldungeon.items.scrolls

import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.EquipableItem
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.items.weapon.melee.MeleeWeapon
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.windows.WndBag

class ScrollOfTransmutation : InventoryScroll() {

    init {
        name = "Scroll of Transmutation"
        inventoryTitle = "Select an item to transmute"
        mode = WndBag.Mode.UPGRADEABLE
    }

    override fun onItemSelected(item: Item) {
        val hero = Dungeon.hero ?: return

        val category = when (item) {
            is MeleeWeapon -> Generator.Category.WEAPON
            is Armor -> Generator.Category.ARMOR
            is Ring -> Generator.Category.RING
            is Wand -> Generator.Category.WAND
            else -> {
                GLog.w("The scroll cannot transmute this item.")
                return
            }
        }

        val lvl = item.level()
        val cursed = item.cursed

        var newItem: Item?
        var attempts = 0
        do {
            newItem = Generator.random(category)
            attempts++
        } while (newItem != null && newItem::class.java == item::class.java && attempts < 20)

        if (newItem == null) {
            GLog.w("The magic fizzles.")
            return
        }

        // Preserve upgrade level and curse status
        if (lvl > 0) {
            newItem.upgrade(lvl)
        } else if (lvl < 0) {
            newItem.degrade(-lvl)
        }
        newItem.cursed = cursed
        newItem.cursedKnown = item.cursedKnown
        newItem.levelKnown = item.levelKnown

        if (item.isEquipped(hero)) {
            (item as? EquipableItem)?.doUnequip(hero, false)
        } else {
            item.detach(hero.belongings.backpack)
        }
        newItem.collect(hero.belongings.backpack)

        curUser?.sprite?.emitter()?.start(Speck.factory(Speck.CHANGE), 0.2f, 10)
        GLog.p("Your %s transforms into %s!", item.name(), newItem.name())
    }

    override fun desc(): String {
        return "This scroll will transform an item into another of the same type, " +
                "preserving its upgrade level. Useful when you find a weapon or armor " +
                "that doesn't suit your needs."
    }

    override fun price(): Int {
        return if (isKnown) 50 * quantity else super.price()
    }
}
