package com.watabou.pixeldungeon.items.crafting

import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.utils.Bundle

class EnchantedBook : Item() {
    var storedEnchantment: Weapon.Enchantment? = null

    init {
        name = "enchanted book"
        image = ItemSpriteSheet.ENCHANTED_BOOK
        unique = true
    }

    override val isUpgradable: Boolean
        get() = false

    override val isIdentified: Boolean
        get() = true

    override fun name(): String {
        val ench = storedEnchantment ?: return name
        return "Book of ${ench.name("Power")}"
    }

    override fun glowing(): ItemSprite.Glowing? {
        return storedEnchantment?.glowing()
    }

    override fun price(): Int = 75

    override fun info(): String =
        if (storedEnchantment != null) {
            "A tome pulsing with captured magical energy. " +
            "It contains the essence of a powerful enchantment, ready to be applied to a weapon at an anvil."
        } else {
            "A tome that once held a powerful enchantment, now spent and empty."
        }

    override fun desc(): String = info()

    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(ENCHANTMENT, storedEnchantment)
    }

    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        storedEnchantment = bundle[ENCHANTMENT] as Weapon.Enchantment?
    }

    companion object {
        private const val ENCHANTMENT = "enchantment"
    }
}
