package com.watabou.pixeldungeon.windows

import com.watabou.noosa.BitmapText
import com.watabou.noosa.BitmapTextMultiline
import com.watabou.noosa.ColorBlock
import com.watabou.noosa.ui.Component
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.crafting.CraftingManager
import com.watabou.pixeldungeon.crafting.Recipe
import com.watabou.pixeldungeon.crafting.RecipeRegistry
import com.watabou.pixeldungeon.crafting.StationType
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog

class WndCrafting(
    private val hero: Hero,
    private val station: StationType
) : Window() {

    private val rows = ArrayList<RecipeRow>()

    init {
        val title = PixelScene.createText(titleFor(station), 9f)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = (WIDTH - title.width()) / 2
        title.y = 0f
        add(title)

        val recipes = RecipeRegistry.forStation(station)

        val content = Component()

        var pos = 0f
        for (recipe in recipes) {
            val row = RecipeRow(recipe)
            row.setRect(0f, pos, WIDTH.toFloat(), 0f)
            content.add(row)
            rows.add(row)
            pos = row.bottom() + GAP
        }

        if (recipes.isEmpty()) {
            val empty = PixelScene.createMultiline("No recipes available.", 6f)
            empty.maxWidth = WIDTH
            empty.measure()
            empty.x = 0f
            empty.y = 0f
            content.add(empty)
            pos = empty.height() + GAP
        }

        content.setSize(WIDTH.toFloat(), pos)

        val listHeight = minOf(pos, MAX_HEIGHT)

        val list = object : ScrollPane(content) {
            override fun onClick(x: Float, y: Float) {
                for (row in rows) {
                    if (row.onClick(x, y)) break
                }
            }
        }
        add(list)
        resize(WIDTH, (title.height() + GAP + listHeight).toInt())
        list.setRect(0f, title.height() + GAP, WIDTH.toFloat(), listHeight)
    }

    private fun titleFor(station: StationType): String {
        return when (station) {
            StationType.CRAFTING_TABLE -> "Crafting Table"
            StationType.FURNACE -> "Furnace"
            StationType.NONE -> "Crafting Kit"
            StationType.ENCHANTING_TABLE -> "Enchanting Table"
            StationType.ANVIL -> "Anvil"
        }
    }

    private inner class RecipeRow(private val recipe: Recipe) : Component() {

        private lateinit var separator: ColorBlock
        private lateinit var outputIcon: ItemSprite
        private lateinit var outputName: BitmapText
        private lateinit var ingredients: BitmapTextMultiline
        private lateinit var craftBtn: RedButton

        override fun createChildren() {
            separator = ColorBlock(WIDTH.toFloat(), 1f, 0xFF222222.toInt())
            add(separator)

            outputIcon = ItemSprite()
            add(outputIcon)

            outputName = PixelScene.createText(9f)
            add(outputName)

            ingredients = PixelScene.createMultiline(6f)
            add(ingredients)

            craftBtn = RedButton("Craft")
            add(craftBtn)
        }

        override fun layout() {
            separator.x = x
            separator.y = y

            // Show output item
            val output = try {
                recipe.outputClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) {
                null
            }

            if (output != null) {
                outputIcon.view(output.image(), output.glowing())
            }
            outputIcon.x = x
            outputIcon.y = y + 2

            val name = (output?.name() ?: recipe.id) +
                if (recipe.outputQuantity > 1) " x${recipe.outputQuantity}" else ""
            outputName.text(name)
            outputName.measure()
            outputName.hardlight(TITLE_COLOR)
            outputName.x = outputIcon.x + outputIcon.width + GAP
            outputName.y = y + 2

            // Build ingredient text with color coding
            val sb = StringBuilder()
            for (input in recipe.inputs) {
                val inputItem = try {
                    input.itemClass.getDeclaredConstructor().newInstance()
                } catch (e: Exception) {
                    null
                }
                val inputName = inputItem?.name() ?: input.itemClass.simpleName
                val has = CraftingManager.hasIngredient(hero, input)
                val color = if (has) "_" else ""
                if (sb.isNotEmpty()) sb.append(", ")
                sb.append("$inputName x${input.quantity}")
                if (!has) sb.append(" (!)")
            }
            if (recipe.station != StationType.NONE) {
                sb.append(" [${recipe.station.name}]")
            }
            ingredients.text(sb.toString())
            ingredients.maxWidth = (width - BUTTON_WIDTH - GAP * 2).toInt()
            ingredients.measure()
            ingredients.x = x + 2
            ingredients.y = outputName.y + outputName.height() + 2

            // Color ingredients based on availability
            val canCraft = CraftingManager.canCraft(hero, recipe)
            if (!canCraft) {
                ingredients.hardlight(0xFF4444)
            } else {
                ingredients.hardlight(0x44FF44)
            }

            craftBtn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
            craftBtn.setPos(x + width - BUTTON_WIDTH, y + 2)
            craftBtn.enable(canCraft)

            height = maxOf(
                ingredients.y + ingredients.height() + GAP - y,
                craftBtn.height() + 4
            )
        }

        fun onClick(x: Float, y: Float): Boolean {
            if (inside(x, y) && CraftingManager.canCraft(hero, recipe)) {
                doCraft()
                return true
            }
            return false
        }

        private fun doCraft() {
            val result = CraftingManager.craft(hero, recipe)
            if (result != null) {
                if (result.collect(hero.belongings.backpack)) {
                    GLog.p("You crafted %s.", result.name())
                } else {
                    val level = com.watabou.pixeldungeon.Dungeon.level ?: return
                    level.drop(result, hero.pos).sprite?.drop()
                    GLog.p("You crafted %s. It fell to the floor.", result.name())
                }
                // Refresh window
                hide()
                GameScene.show(WndCrafting(hero, station))
            }
        }
    }

    companion object {
        private const val WIDTH = 120
        private const val MAX_HEIGHT = 160f
        private const val GAP = 2f
        private const val BUTTON_WIDTH = 36f
        private const val BUTTON_HEIGHT = 16f
    }
}
