package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.items.weapon.enchantments.FireAspect
import com.watabou.pixeldungeon.items.weapon.enchantments.FrostAspect
import com.watabou.pixeldungeon.items.weapon.enchantments.Knockback
import com.watabou.pixeldungeon.items.weapon.enchantments.Reach
import com.watabou.pixeldungeon.items.weapon.enchantments.Sharpness
import com.watabou.pixeldungeon.items.weapon.enchantments.Soulbound
import com.watabou.pixeldungeon.items.weapon.enchantments.SweepingEdge
import com.watabou.pixeldungeon.items.weapon.enchantments.ThunderAspect
import com.watabou.pixeldungeon.items.weapon.enchantments.Vampirism
import com.watabou.utils.Random

object EnchantmentRegistry {

    data class EnchantmentEntry(
        val enchantmentClass: Class<out Weapon.Enchantment>,
        val tier: EnchantmentTier,
        val displayName: String
    )

    private val entries = ArrayList<EnchantmentEntry>()

    init {
        // Tier 1
        register(EnchantmentEntry(Sharpness::class.java, EnchantmentTier.TIER_1, "Sharpness"))
        register(EnchantmentEntry(Knockback::class.java, EnchantmentTier.TIER_1, "Knockback"))
        register(EnchantmentEntry(Reach::class.java, EnchantmentTier.TIER_1, "Reach"))

        // Tier 2
        register(EnchantmentEntry(FireAspect::class.java, EnchantmentTier.TIER_2, "Fire Aspect"))
        register(EnchantmentEntry(FrostAspect::class.java, EnchantmentTier.TIER_2, "Frost Aspect"))
        register(EnchantmentEntry(ThunderAspect::class.java, EnchantmentTier.TIER_2, "Thunder Aspect"))

        // Tier 3
        register(EnchantmentEntry(Vampirism::class.java, EnchantmentTier.TIER_3, "Vampirism"))
        register(EnchantmentEntry(Soulbound::class.java, EnchantmentTier.TIER_3, "Soulbound"))
        register(EnchantmentEntry(SweepingEdge::class.java, EnchantmentTier.TIER_3, "Sweeping Edge"))
    }

    fun register(entry: EnchantmentEntry) {
        entries.add(entry)
    }

    fun all(): List<EnchantmentEntry> = entries

    fun tierOf(enchClass: Class<out Weapon.Enchantment>): EnchantmentTier? {
        return entries.find { it.enchantmentClass == enchClass }?.tier
    }

    fun generateOptions(heroLevel: Int, dustCount: Int): List<EnchantmentEntry> {
        val affordable = entries.filter { it.tier.dustCost <= dustCount }
        if (affordable.isEmpty()) return emptyList()

        val shuffled = ArrayList(affordable)
        for (i in shuffled.size - 1 downTo 1) {
            val j = Random.Int(i + 1)
            val tmp = shuffled[i]
            shuffled[i] = shuffled[j]
            shuffled[j] = tmp
        }
        return shuffled.take(3)
    }

    fun createEnchantment(entry: EnchantmentEntry): Weapon.Enchantment? {
        return try {
            entry.enchantmentClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            null
        }
    }
}
