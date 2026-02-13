package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapText
import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.crafting.EnchantmentRegistry
import com.watabou.pixeldungeon.crafting.EnchantmentTier
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.crafting.ArcaneDust
import com.watabou.pixeldungeon.items.crafting.BlankTome
import com.watabou.pixeldungeon.items.crafting.EnchantedBook
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

class WndEnchanting(private val hero: Hero, private val selectedItem: Item? = null) : Window() {

    private lateinit var itemBtn: WndBlacksmith.ItemButton
    private lateinit var dustLabel: BitmapText
    private lateinit var disenchantBtn: RedButton
    private lateinit var grindBtn: RedButton

    private var currentOptions = emptyList<EnchantmentRegistry.EnchantmentEntry>()

    private val itemSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null) {
                hide()
                GameScene.show(WndEnchanting(hero, item))
            }
        }
    }

    private val scrollSelector = object : WndBag.Listener {
        override fun onSelect(item: Item?) {
            if (item != null && item is Scroll) {
                item.detach(hero.belongings.backpack)
                val dust = ArcaneDust()
                dust.quantity = 1
                if (dust.collect(hero.belongings.backpack)) {
                    GLog.p("You ground the scroll into arcane dust.")
                } else {
                    val level = com.watabou.pixeldungeon.Dungeon.level ?: return
                    level.drop(dust, hero.pos).sprite?.drop()
                }
                Sample.play(Assets.SND_SHATTER)
                // Refresh window
                hide()
                GameScene.show(WndEnchanting(hero, selectedItem))
            }
        }
    }

    init {
        val title = PixelScene.createText("Enchanting Table", 9f)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = (WIDTH - title.width()) / 2
        title.y = 0f
        add(title)

        var pos = title.height() + GAP

        // Item slot
        val selectLabel = PixelScene.createText("Place item:", 6f)
        selectLabel.measure()
        selectLabel.x = 0f
        selectLabel.y = pos + 6
        add(selectLabel)

        itemBtn = object : WndBlacksmith.ItemButton() {
            override fun onClick() {
                GameScene.selectItem(itemSelector, WndBag.Mode.ENCHANTABLE, TXT_SELECT)
            }
        }
        itemBtn.setRect(selectLabel.width() + GAP, pos, BTN_SIZE.toFloat(), BTN_SIZE.toFloat())
        add(itemBtn)
        if (selectedItem != null) {
            itemBtn.item(selectedItem)
        }

        // Dust count
        dustLabel = PixelScene.createText(9f)
        dustLabel.x = itemBtn.right() + GAP * 2
        dustLabel.y = pos + 6
        add(dustLabel)
        updateDustLabel()

        pos = itemBtn.bottom() + GAP

        // Disenchant button
        disenchantBtn = object : RedButton(TXT_DISENCHANT) {
            override fun onClick() {
                doDisenchant()
            }
        }
        disenchantBtn.setRect(0f, pos, (WIDTH / 2 - GAP).toFloat(), 18f)
        val item = selectedItem
        disenchantBtn.enable(item is Weapon && item.enchantment != null)
        add(disenchantBtn)

        // Grind scroll button
        grindBtn = object : RedButton(TXT_GRIND) {
            override fun onClick() {
                GameScene.selectItem(scrollSelector, WndBag.Mode.SCROLL, TXT_SELECT_SCROLL)
            }
        }
        grindBtn.setRect(disenchantBtn.right() + GAP * 2, pos, (WIDTH / 2 - GAP).toFloat(), 18f)
        add(grindBtn)

        pos = disenchantBtn.bottom() + GAP * 2

        // Generate enchantment options
        if (item != null) {
            val dust = dustCount()
            currentOptions = EnchantmentRegistry.generateOptions(hero.lvl, dust)

            for (i in currentOptions.indices) {
                val entry = currentOptions[i]
                val tier = entry.tier
                val costText = buildCostText(tier)
                val capturedIndex = i
                val btn = object : RedButton("${entry.displayName} ($costText)") {
                    override fun onClick() {
                        doEnchant(currentOptions[capturedIndex])
                    }
                }
                btn.setRect(0f, pos, WIDTH.toFloat(), 18f)
                btn.enable(canAfford(tier))
                add(btn)
                pos += 20f
            }

            if (currentOptions.isEmpty()) {
                val noOptions = PixelScene.createText("Not enough dust.", 6f)
                noOptions.hardlight(0xFF4444)
                noOptions.measure()
                noOptions.x = 0f
                noOptions.y = pos
                add(noOptions)
                pos += noOptions.height() + GAP
            }
        }

        // Recipes button — access crafting recipes for this station
        pos += GAP
        val recipesBtn = object : RedButton(TXT_RECIPES) {
            override fun onClick() {
                hide()
                GameScene.show(WndCrafting(hero, com.watabou.pixeldungeon.crafting.StationType.ENCHANTING_TABLE))
            }
        }
        recipesBtn.setRect(0f, pos, WIDTH.toFloat(), 18f)
        add(recipesBtn)
        pos += recipesBtn.height()

        resize(WIDTH, pos.toInt())
    }

    private fun dustCount(): Int {
        var count = 0
        for (item in hero.belongings.backpack.items) {
            if (item is ArcaneDust) count += item.quantity
        }
        return count
    }

    private fun updateDustLabel() {
        dustLabel.text("Dust: ${dustCount()}")
        dustLabel.measure()
    }

    private fun buildCostText(tier: EnchantmentTier): String {
        val parts = ArrayList<String>()
        parts.add("${tier.dustCost} Dust")
        if (tier.xpPercent > 0f) {
            parts.add("${(tier.xpPercent * 100).toInt()}% XP")
        }
        if (tier.levelCost > 0) {
            parts.add("${tier.levelCost} Level")
        }
        return parts.joinToString(" + ")
    }

    private fun canAfford(tier: EnchantmentTier): Boolean {
        if (dustCount() < tier.dustCost) return false
        if (tier.xpPercent > 0f) {
            val xpCost = (hero.maxExp() * tier.xpPercent).toInt()
            if (hero.exp < xpCost) return false
        }
        if (tier.levelCost > 0 && hero.lvl < tier.levelCost + 1) return false
        return true
    }

    private fun consumeDust(amount: Int) {
        var remaining = amount
        val iterator = hero.belongings.backpack.items.iterator()
        while (iterator.hasNext() && remaining > 0) {
            val item = iterator.next()
            if (item is ArcaneDust) {
                if (item.quantity <= remaining) {
                    remaining -= item.quantity
                    iterator.remove()
                } else {
                    item.quantity -= remaining
                    remaining = 0
                }
            }
        }
    }

    private fun doEnchant(entry: EnchantmentRegistry.EnchantmentEntry) {
        val item = selectedItem ?: return
        val tier = entry.tier

        if (!canAfford(tier)) return

        val enchantment = EnchantmentRegistry.createEnchantment(entry) ?: return

        // Pay costs
        consumeDust(tier.dustCost)

        if (tier.xpPercent > 0f) {
            val xpCost = (hero.maxExp() * tier.xpPercent).toInt()
            hero.exp -= xpCost
        }

        if (tier.levelCost > 0) {
            hero.lvl -= tier.levelCost
        }

        // Apply enchantment
        when (item) {
            is Weapon -> {
                item.enchant(enchantment)
                GLog.p("Your %s glows with magical energy!", item.name())
            }
            is BlankTome -> {
                item.detach(hero.belongings.backpack)
                val book = EnchantedBook()
                book.storedEnchantment = enchantment
                if (book.collect(hero.belongings.backpack)) {
                    GLog.p("The tome absorbs the enchantment!")
                } else {
                    val level = com.watabou.pixeldungeon.Dungeon.level ?: return
                    level.drop(book, hero.pos).sprite?.drop()
                }
            }
        }

        Sample.play(Assets.SND_EVOKE)
        hide()
    }

    private fun doDisenchant() {
        val item = selectedItem
        if (item !is Weapon || item.enchantment == null) return

        item.enchant(null)
        val dustAmount = Random.IntRange(1, 3)
        val dust = ArcaneDust()
        dust.quantity = dustAmount
        if (dust.collect(hero.belongings.backpack)) {
            GLog.p("You extracted %d arcane dust.", dustAmount)
        } else {
            val level = com.watabou.pixeldungeon.Dungeon.level ?: return
            level.drop(dust, hero.pos).sprite?.drop()
        }

        Sample.play(Assets.SND_SHATTER)
        hide()
    }

    companion object {
        private const val WIDTH = 120
        private const val GAP = 2f
        private const val BTN_SIZE = 28
        private const val TXT_SELECT = "Select an item to enchant"
        private const val TXT_SELECT_SCROLL = "Select a scroll to grind"
        private const val TXT_DISENCHANT = "Disenchant"
        private const val TXT_GRIND = "Grind Scroll"
        private const val TXT_RECIPES = "Recipes"
    }
}
