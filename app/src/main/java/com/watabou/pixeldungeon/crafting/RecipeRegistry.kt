package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.armor.crafted.ChainVest
import com.watabou.pixeldungeon.items.armor.crafted.DiamondMail
import com.watabou.pixeldungeon.items.armor.crafted.IronPlate
import com.watabou.pixeldungeon.items.armor.crafted.LeatherTunic
import com.watabou.pixeldungeon.items.crafting.Bandage
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.CobblestoneBlock
import com.watabou.pixeldungeon.items.crafting.CraftedTorch
import com.watabou.pixeldungeon.items.crafting.DiamondShard
import com.watabou.pixeldungeon.items.crafting.Fiber
import com.watabou.pixeldungeon.items.crafting.GoldIngot
import com.watabou.pixeldungeon.items.crafting.GoldOre
import com.watabou.pixeldungeon.items.crafting.IronIngot
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.items.crafting.Leather
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.crafting.WoodPlank
import com.watabou.pixeldungeon.items.food.ChargrilledMeat
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.quest.DarkGold
import com.watabou.pixeldungeon.items.weapon.melee.crafted.DiamondBlade
import com.watabou.pixeldungeon.items.weapon.melee.crafted.IronMace
import com.watabou.pixeldungeon.items.weapon.melee.crafted.IronSword
import com.watabou.pixeldungeon.items.weapon.melee.crafted.StoneAxe
import com.watabou.pixeldungeon.items.weapon.melee.crafted.StoneDagger
import com.watabou.pixeldungeon.items.weapon.melee.crafted.WoodenClub

object RecipeRegistry {

    private val recipes = ArrayList<Recipe>()

    init {
        // Phase 1: Basic material processing
        register(Recipe(
            id = "wood_plank",
            inputs = listOf(RecipeInput(Stick::class.java, 3)),
            outputClass = WoodPlank::class.java,
            outputQuantity = 2
        ))

        register(Recipe(
            id = "cobblestone_block",
            inputs = listOf(RecipeInput(Cobblestone::class.java, 4)),
            outputClass = CobblestoneBlock::class.java,
            outputQuantity = 1
        ))

        register(Recipe(
            id = "iron_ingot",
            inputs = listOf(RecipeInput(IronOre::class.java, 1)),
            outputClass = IronIngot::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "gold_ingot",
            inputs = listOf(RecipeInput(GoldOre::class.java, 1)),
            outputClass = GoldIngot::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "chargrilled_meat",
            inputs = listOf(RecipeInput(MysteryMeat::class.java, 1)),
            outputClass = ChargrilledMeat::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "dark_gold_ingot",
            inputs = listOf(RecipeInput(DarkGold::class.java, 1)),
            outputClass = GoldIngot::class.java,
            outputQuantity = 2,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "fired_cobblestone_block",
            inputs = listOf(RecipeInput(Cobblestone::class.java, 4)),
            outputClass = CobblestoneBlock::class.java,
            outputQuantity = 2,
            station = StationType.FURNACE
        ))

        // Phase 2: Consumables
        register(Recipe(
            id = "crafted_torch",
            inputs = listOf(
                RecipeInput(Stick::class.java, 1),
                RecipeInput(Fiber::class.java, 1)
            ),
            outputClass = CraftedTorch::class.java,
            outputQuantity = 1
        ))

        register(Recipe(
            id = "bandage",
            inputs = listOf(RecipeInput(Fiber::class.java, 3)),
            outputClass = Bandage::class.java,
            outputQuantity = 1
        ))

        // Phase 3: Crafted weapons
        register(Recipe(
            id = "wooden_club",
            inputs = listOf(RecipeInput(WoodPlank::class.java, 3)),
            outputClass = WoodenClub::class.java,
            outputQuantity = 1
        ))

        register(Recipe(
            id = "stone_dagger",
            inputs = listOf(
                RecipeInput(Cobblestone::class.java, 2),
                RecipeInput(Stick::class.java, 1)
            ),
            outputClass = StoneDagger::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "stone_axe",
            inputs = listOf(
                RecipeInput(Cobblestone::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = StoneAxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "iron_sword",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 2),
                RecipeInput(WoodPlank::class.java, 1)
            ),
            outputClass = IronSword::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "iron_mace",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 3),
                RecipeInput(Stick::class.java, 1)
            ),
            outputClass = IronMace::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "diamond_blade",
            inputs = listOf(
                RecipeInput(DiamondShard::class.java, 2),
                RecipeInput(IronIngot::class.java, 1),
                RecipeInput(WoodPlank::class.java, 1)
            ),
            outputClass = DiamondBlade::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        // Phase 3: Crafted armor
        register(Recipe(
            id = "leather_tunic",
            inputs = listOf(RecipeInput(Leather::class.java, 3)),
            outputClass = LeatherTunic::class.java,
            outputQuantity = 1
        ))

        register(Recipe(
            id = "chain_vest",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 2),
                RecipeInput(Leather::class.java, 1)
            ),
            outputClass = ChainVest::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "iron_plate",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 4),
                RecipeInput(Leather::class.java, 1)
            ),
            outputClass = IronPlate::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "diamond_mail",
            inputs = listOf(
                RecipeInput(DiamondShard::class.java, 2),
                RecipeInput(IronIngot::class.java, 2),
                RecipeInput(Leather::class.java, 1)
            ),
            outputClass = DiamondMail::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))
    }

    fun register(recipe: Recipe) {
        recipes.add(recipe)
    }

    fun all(): List<Recipe> = recipes

    fun forStation(station: StationType): List<Recipe> = when (station) {
        StationType.FURNACE -> recipes.filter { it.station == StationType.FURNACE }
        StationType.CRAFTING_TABLE -> recipes.filter {
            it.station == StationType.CRAFTING_TABLE || it.station == StationType.NONE
        }
        StationType.NONE -> recipes.filter { it.station == StationType.NONE }
    }

    fun byId(id: String): Recipe? = recipes.find { it.id == id }
}
