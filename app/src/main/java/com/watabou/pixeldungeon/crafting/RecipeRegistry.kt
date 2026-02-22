package com.watabou.pixeldungeon.crafting

import com.watabou.pixeldungeon.items.crafting.StorageChestItem
import com.watabou.pixeldungeon.items.crafting.DimensionalChestItem
import com.watabou.pixeldungeon.items.crafting.EyeOfEnder
import com.watabou.pixeldungeon.items.crafting.Bone
import com.watabou.pixeldungeon.items.crafting.Bonemeal
import com.watabou.pixeldungeon.items.crafting.Hoe
import com.watabou.pixeldungeon.items.crafting.WoodenBowl
import com.watabou.pixeldungeon.items.crafting.WaterBucket
import com.watabou.pixeldungeon.items.food.farming.Wheat
import com.watabou.pixeldungeon.items.food.farming.Bread
import com.watabou.pixeldungeon.items.food.farming.Potato
import com.watabou.pixeldungeon.items.food.farming.BakedPotato
import com.watabou.pixeldungeon.items.food.farming.Carrot
import com.watabou.pixeldungeon.items.food.farming.RabbitStew
import com.watabou.pixeldungeon.items.food.farming.PlanterBox
import com.watabou.pixeldungeon.items.crafting.ArcaneDust
import com.watabou.pixeldungeon.items.crafting.ArcaneOre
import com.watabou.pixeldungeon.items.crafting.SpikeTrapItem
import com.watabou.pixeldungeon.items.crafting.TorchHolderItem
import com.watabou.pixeldungeon.items.crafting.SupportBeamItem
import com.watabou.pixeldungeon.items.crafting.SafeRoomBlueprint
import com.watabou.pixeldungeon.items.crafting.MiniForgeItem
import com.watabou.pixeldungeon.items.crafting.ResourceCacheItem
import com.watabou.pixeldungeon.items.crafting.WoodenPickaxe
import com.watabou.pixeldungeon.items.crafting.WoodenAxe
import com.watabou.pixeldungeon.items.crafting.IronAxe
import com.watabou.pixeldungeon.items.crafting.DiamondAxe
import com.watabou.pixeldungeon.items.crafting.Log
import com.watabou.pixeldungeon.items.crafting.Bark
import com.watabou.pixeldungeon.items.crafting.Rope
import com.watabou.pixeldungeon.items.crafting.WoodenFence
import com.watabou.pixeldungeon.items.crafting.StonePickaxe
import com.watabou.pixeldungeon.items.crafting.IronPickaxe
import com.watabou.pixeldungeon.items.crafting.DiamondPickaxe
import com.watabou.pixeldungeon.items.crafting.WoodBarricadeItem
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

        // Phase 4: Storage chests
        register(Recipe(
            id = "storage_chest",
            inputs = listOf(RecipeInput(WoodPlank::class.java, 8)),
            outputClass = StorageChestItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "dimensional_chest",
            inputs = listOf(
                RecipeInput(StorageChestItem::class.java, 1),
                RecipeInput(EyeOfEnder::class.java, 1)
            ),
            outputClass = DimensionalChestItem::class.java,
            outputQuantity = 1,
            station = StationType.ENCHANTING_TABLE
        ))

        // Phase 5: Farming recipes
        register(Recipe(
            id = "hoe",
            inputs = listOf(
                RecipeInput(Stick::class.java, 2),
                RecipeInput(Cobblestone::class.java, 1)
            ),
            outputClass = Hoe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "wooden_bowl",
            inputs = listOf(RecipeInput(WoodPlank::class.java, 2)),
            outputClass = WoodenBowl::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "planter_box",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 4),
                RecipeInput(Cobblestone::class.java, 1)
            ),
            outputClass = PlanterBox::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "water_bucket",
            inputs = listOf(RecipeInput(IronIngot::class.java, 3)),
            outputClass = WaterBucket::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "bonemeal",
            inputs = listOf(RecipeInput(Bone::class.java, 3)),
            outputClass = Bonemeal::class.java,
            outputQuantity = 3
        ))

        register(Recipe(
            id = "bread",
            inputs = listOf(RecipeInput(Wheat::class.java, 3)),
            outputClass = Bread::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "baked_potato",
            inputs = listOf(RecipeInput(Potato::class.java, 1)),
            outputClass = BakedPotato::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        register(Recipe(
            id = "rabbit_stew",
            inputs = listOf(
                RecipeInput(ChargrilledMeat::class.java, 1),
                RecipeInput(Carrot::class.java, 1),
                RecipeInput(BakedPotato::class.java, 1),
                RecipeInput(WoodenBowl::class.java, 1)
            ),
            outputClass = RabbitStew::class.java,
            outputQuantity = 1,
            station = StationType.FURNACE
        ))

        // Phase 6: Mining pickaxes
        register(Recipe(
            id = "wooden_pickaxe",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = WoodenPickaxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "stone_pickaxe",
            inputs = listOf(
                RecipeInput(Cobblestone::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = StonePickaxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "iron_pickaxe",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = IronPickaxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "diamond_pickaxe",
            inputs = listOf(
                RecipeInput(DiamondShard::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = DiamondPickaxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        // Phase 6: Building items
        register(Recipe(
            id = "spike_trap",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 2),
                RecipeInput(Stick::class.java, 1)
            ),
            outputClass = SpikeTrapItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "torch_holder",
            inputs = listOf(
                RecipeInput(Stick::class.java, 2),
                RecipeInput(IronIngot::class.java, 1),
                RecipeInput(Fiber::class.java, 1)
            ),
            outputClass = TorchHolderItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "support_beam",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 3),
                RecipeInput(CobblestoneBlock::class.java, 1)
            ),
            outputClass = SupportBeamItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "safe_room",
            inputs = listOf(
                RecipeInput(CobblestoneBlock::class.java, 6),
                RecipeInput(IronIngot::class.java, 2),
                RecipeInput(WoodPlank::class.java, 2)
            ),
            outputClass = SafeRoomBlueprint::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "mini_forge",
            inputs = listOf(
                RecipeInput(CobblestoneBlock::class.java, 4),
                RecipeInput(IronIngot::class.java, 2)
            ),
            outputClass = MiniForgeItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "resource_cache",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 6),
                RecipeInput(IronIngot::class.java, 1)
            ),
            outputClass = ResourceCacheItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        // Arcane ore smelting
        register(Recipe(
            id = "arcane_smelt",
            inputs = listOf(RecipeInput(ArcaneOre::class.java, 1)),
            outputClass = ArcaneDust::class.java,
            outputQuantity = 2,
            station = StationType.FURNACE
        ))

        // Wood barricade
        register(Recipe(
            id = "wood_barricade",
            inputs = listOf(RecipeInput(WoodPlank::class.java, 4)),
            outputClass = WoodBarricadeItem::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        // Phase 7: Woodcutting - material processing
        register(Recipe(
            id = "log_to_planks",
            inputs = listOf(RecipeInput(Log::class.java, 1)),
            outputClass = WoodPlank::class.java,
            outputQuantity = 3,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "log_to_sticks",
            inputs = listOf(RecipeInput(Log::class.java, 1)),
            outputClass = Stick::class.java,
            outputQuantity = 4
        ))

        register(Recipe(
            id = "bark_rope",
            inputs = listOf(
                RecipeInput(Bark::class.java, 2),
                RecipeInput(Fiber::class.java, 1)
            ),
            outputClass = Rope::class.java,
            outputQuantity = 2
        ))

        // Phase 7: Woodcutting - axes
        register(Recipe(
            id = "wooden_axe",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = WoodenAxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "iron_axe",
            inputs = listOf(
                RecipeInput(IronIngot::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = IronAxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        register(Recipe(
            id = "diamond_axe",
            inputs = listOf(
                RecipeInput(DiamondShard::class.java, 3),
                RecipeInput(Stick::class.java, 2)
            ),
            outputClass = DiamondAxe::class.java,
            outputQuantity = 1,
            station = StationType.CRAFTING_TABLE
        ))

        // Phase 7: Woodcutting - building items
        register(Recipe(
            id = "wooden_fence",
            inputs = listOf(
                RecipeInput(WoodPlank::class.java, 3),
                RecipeInput(Stick::class.java, 1)
            ),
            outputClass = WoodenFence::class.java,
            outputQuantity = 2,
            station = StationType.CRAFTING_TABLE
        ))

        // Phase 7: Resin torch (better than fiber torch)
        register(Recipe(
            id = "resin_torch",
            inputs = listOf(
                RecipeInput(Stick::class.java, 1),
                RecipeInput(com.watabou.pixeldungeon.items.crafting.Resin::class.java, 1)
            ),
            outputClass = CraftedTorch::class.java,
            outputQuantity = 2
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
        StationType.ENCHANTING_TABLE -> recipes.filter { it.station == StationType.ENCHANTING_TABLE }
        StationType.ANVIL -> recipes.filter { it.station == StationType.ANVIL }
    }

    fun byId(id: String): Recipe? = recipes.find { it.id == id }
}
