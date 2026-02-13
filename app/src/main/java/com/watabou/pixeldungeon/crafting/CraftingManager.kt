package com.watabou.pixeldungeon.crafting

import com.watabou.noosa.audio.Sample
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.crafting.IronIngot
import com.watabou.pixeldungeon.items.crafting.Leather
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.utils.Random

object CraftingManager {

    fun availableRecipes(hero: Hero, station: StationType): List<Recipe> {
        return RecipeRegistry.forStation(station).filter { recipe ->
            // Show recipe if hero has at least one of the primary ingredient
            val primary = recipe.inputs.first()
            hasAny(hero, primary)
        }
    }

    fun canCraft(hero: Hero, recipe: Recipe): Boolean {
        return recipe.inputs.all { hasIngredient(hero, it) }
    }

    fun craft(hero: Hero, recipe: Recipe): Item? {
        if (Dungeon.bossLevel()) {
            GLog.w("You can't craft here!")
            return null
        }

        if (!canCraft(hero, recipe)) return null

        consumeIngredients(hero, recipe)

        val output = try {
            recipe.outputClass.getDeclaredConstructor().newInstance()
        } catch (e: Exception) {
            return null
        }
        output.quantity = recipe.outputQuantity

        Sample.play(Assets.SND_EVOKE)

        return output
    }

    fun salvage(item: Item): List<Item> {
        val results = ArrayList<Item>()

        // Find any recipe that produces this item's class
        val recipe = RecipeRegistry.all().find { it.outputClass == item.javaClass }

        if (recipe != null) {
            // Return 20-40% of primary material cost
            val primary = recipe.inputs.first()
            val returnCount = maxOf(1, (primary.quantity * Random.Float(0.2f, 0.4f)).toInt())
            val mat = try {
                primary.itemClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                return results
            }
            mat.quantity = returnCount
            results.add(mat)
        } else {
            // Generic fallback based on item type
            val mat: Item = if (item is Armor) {
                Leather()
            } else {
                IronIngot()
            }
            mat.quantity = 1
            results.add(mat)
        }

        return results
    }

    fun hasIngredient(hero: Hero, input: RecipeInput): Boolean {
        var count = 0
        for (item in hero.belongings.backpack) {
            if (input.itemClass.isInstance(item)) {
                count += item.quantity
                if (count >= input.quantity) return true
            }
        }
        return false
    }

    fun consumeIngredients(hero: Hero, recipe: Recipe) {
        for (input in recipe.inputs) {
            var remaining = input.quantity
            // Iterate over a snapshot to avoid ConcurrentModificationException
            val snapshot = ArrayList<Item>()
            for (item in hero.belongings.backpack) {
                snapshot.add(item)
            }
            for (item in snapshot) {
                if (remaining <= 0) break
                if (input.itemClass.isInstance(item)) {
                    val toTake = minOf(remaining, item.quantity)
                    remaining -= toTake
                    item.quantity -= toTake
                    if (item.quantity <= 0) {
                        item.detachAll(hero.belongings.backpack)
                    }
                }
            }
        }
    }

    private fun hasAny(hero: Hero, input: RecipeInput): Boolean {
        for (item in hero.belongings.backpack) {
            if (input.itemClass.isInstance(item)) return true
        }
        return false
    }
}
