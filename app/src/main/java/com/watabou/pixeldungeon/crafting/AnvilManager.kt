package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.EnchantedBook
import com.watabou.pixeldungeon.items.weapon.Weapon
import kotlin.math.min

object AnvilManager {

    fun canRepair(item1: Item, item2: Item): Boolean {
        if (item1 === item2) return false
        if (item1 !is Weapon || item2 !is Weapon) return false
        return item1::class.java == item2::class.java
    }

    fun repair(hero: Hero, item1: Item, item2: Item): Item? {
        if (!canRepair(item1, item2)) return null

        val combined = item1.durability() + item2.durability()
        val bonus = (item1.maxDurability() * 0.10f).toInt()
        val newDurability = min(combined + bonus, item1.maxDurability())

        item1.setDurability(newDurability)
        item2.detach(hero.belongings.backpack)

        return item1
    }

    fun canApplyBook(weapon: Item, book: Item): Boolean {
        if (weapon !is Weapon) return false
        if (book !is EnchantedBook) return false
        return book.storedEnchantment != null
    }

    fun applyBook(hero: Hero, weapon: Item, book: Item): Boolean {
        if (!canApplyBook(weapon, book)) return false

        weapon as Weapon
        book as EnchantedBook

        val enchantment = book.storedEnchantment ?: return false
        val tier = EnchantmentRegistry.tierOf(enchantment::class.java) ?: return false

        // Pay XP cost
        val xpCost = (hero.maxExp() * tier.xpPercent).toInt()
        if (xpCost > 0 && hero.exp < xpCost) return false
        hero.exp -= xpCost

        // Pay level cost
        if (tier.levelCost > 0 && hero.lvl < tier.levelCost + 1) return false
        if (tier.levelCost > 0) {
            hero.lvl -= tier.levelCost
        }

        // Apply enchantment and consume book
        weapon.enchant(enchantment)
        book.detach(hero.belongings.backpack)

        return true
    }
}
