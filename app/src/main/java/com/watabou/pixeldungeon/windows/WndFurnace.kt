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
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.scenes.PixelScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.RedButton
import com.watabou.pixeldungeon.ui.ScrollPane
import com.watabou.pixeldungeon.ui.Window
import com.watabou.pixeldungeon.utils.GLog

class WndFurnace(private val hero: Hero) : Window() {

    private val rows = ArrayList<SmeltRow>()

    init {
        val title = PixelScene.createText("Furnace", 9f)
        title.hardlight(TITLE_COLOR)
        title.measure()
        title.x = (WIDTH - title.width()) / 2
        title.y = 0f
        add(title)

        val recipes = RecipeRegistry.forStation(StationType.FURNACE)

        val content = Component()
        var pos = 0f

        for (recipe in recipes) {
            val row = SmeltRow(recipe)
            row.setRect(0f, pos, WIDTH.toFloat(), 0f)
            content.add(row)
            rows.add(row)
            pos = row.bottom() + GAP
        }

        if (recipes.isEmpty()) {
            val empty = PixelScene.createMultiline("No smelting recipes available.", 6f)
            empty.maxWidth = WIDTH
            empty.measure()
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

    private inner class SmeltRow(private val recipe: Recipe) : Component() {

        private lateinit var separator: ColorBlock
        private lateinit var inputIcon: ItemSprite
        private lateinit var arrow: BitmapText
        private lateinit var outputIcon: ItemSprite
        private lateinit var label: BitmapTextMultiline
        private lateinit var smeltBtn: RedButton

        override fun createChildren() {
            separator = ColorBlock(WIDTH.toFloat(), 1f, 0xFF222222.toInt())
            add(separator)

            inputIcon = ItemSprite()
            add(inputIcon)

            arrow = PixelScene.createText("->", 7f)
            add(arrow)

            outputIcon = ItemSprite()
            add(outputIcon)

            label = PixelScene.createMultiline(6f)
            add(label)

            smeltBtn = RedButton("Smelt")
            add(smeltBtn)
        }

        override fun layout() {
            separator.x = x
            separator.y = y

            val input = try {
                recipe.inputs.first().itemClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) { null }

            val output = try {
                recipe.outputClass.getDeclaredConstructor().newInstance()
            } catch (e: Exception) { null }

            if (input != null) inputIcon.view(input.image(), input.glowing())
            inputIcon.x = x
            inputIcon.y = y + 2

            arrow.text("->")
            arrow.measure()
            arrow.x = inputIcon.x + inputIcon.width + 2
            arrow.y = y + 6

            if (output != null) outputIcon.view(output.image(), output.glowing())
            outputIcon.x = arrow.x + arrow.width() + 2
            outputIcon.y = y + 2

            val inputName = input?.name() ?: "?"
            val outputName = output?.name() ?: "?"
            val qty = recipe.inputs.first().quantity
            val outQty = recipe.outputQuantity
            val text = "${inputName} x$qty -> ${outputName}" +
                if (outQty > 1) " x$outQty" else ""
            label.text(text)
            label.maxWidth = (width - BUTTON_WIDTH - GAP * 2).toInt()
            label.measure()
            label.x = x + 2
            label.y = outputIcon.y + outputIcon.height + 1

            val canSmelt = CraftingManager.canCraft(hero, recipe)
            if (canSmelt) {
                label.hardlight(0x44FF44)
            } else {
                label.hardlight(0xFF4444)
            }

            smeltBtn.setSize(BUTTON_WIDTH, BUTTON_HEIGHT)
            smeltBtn.setPos(x + width - BUTTON_WIDTH, y + 2)
            smeltBtn.enable(canSmelt)

            height = maxOf(
                label.y + label.height() + GAP - y,
                smeltBtn.height() + 4
            )
        }

        fun onClick(x: Float, y: Float): Boolean {
            if (inside(x, y) && CraftingManager.canCraft(hero, recipe)) {
                doSmelt()
                return true
            }
            return false
        }

        private fun doSmelt() {
            val result = CraftingManager.craft(hero, recipe)
            if (result != null) {
                if (result.collect(hero.belongings.backpack)) {
                    GLog.p("You smelted %s.", result.name())
                } else {
                    val level = com.watabou.pixeldungeon.Dungeon.level ?: return
                    level.drop(result, hero.pos).sprite?.drop()
                    GLog.p("You smelted %s. It fell to the floor.", result.name())
                }
                hide()
                GameScene.show(WndFurnace(hero))
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
