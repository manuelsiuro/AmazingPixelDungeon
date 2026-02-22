package com.watabou.pixeldungeon.levels

import com.watabou.noosa.Game
import com.watabou.noosa.Scene
import com.watabou.noosa.particles.Emitter
import com.watabou.noosa.particles.PixelParticle
import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.DungeonTilemap
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.blobs.Alchemy
import com.watabou.pixeldungeon.actors.blobs.Foliage
import com.watabou.pixeldungeon.actors.blobs.WaterOfHealth
import com.watabou.pixeldungeon.actors.mobs.Rat
import com.watabou.pixeldungeon.actors.mobs.npcs.Shopkeeper
import com.watabou.pixeldungeon.actors.mobs.npcs.VillageElder
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.effects.particles.LeafParticle
import com.watabou.pixeldungeon.effects.particles.SmokeParticle
import com.watabou.pixeldungeon.effects.particles.WindParticle
import com.watabou.pixeldungeon.items.Ankh
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.items.Heap
import com.watabou.pixeldungeon.items.HolyWater
import com.watabou.pixeldungeon.items.Honeypot
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.SmokeBomb
import com.watabou.pixeldungeon.items.Torch
import com.watabou.pixeldungeon.items.Weightstone
import com.watabou.pixeldungeon.items.armor.ClothArmor
import com.watabou.pixeldungeon.items.armor.LeatherArmor
import com.watabou.pixeldungeon.items.bags.MaterialBag
import com.watabou.pixeldungeon.items.bags.SeedPouch
import com.watabou.pixeldungeon.items.crafting.Bark
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.CobblestoneBlock
import com.watabou.pixeldungeon.items.crafting.DiamondAxe
import com.watabou.pixeldungeon.items.crafting.DiamondShard
import com.watabou.pixeldungeon.items.crafting.Fiber
import com.watabou.pixeldungeon.items.crafting.GoldOre
import com.watabou.pixeldungeon.items.crafting.IronAxe
import com.watabou.pixeldungeon.items.crafting.IronIngot
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.items.crafting.Leather
import com.watabou.pixeldungeon.items.crafting.Log
import com.watabou.pixeldungeon.items.crafting.Resin
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.crafting.ArcaneDust
import com.watabou.pixeldungeon.items.crafting.ArcaneOre
import com.watabou.pixeldungeon.items.crafting.BlankTome
import com.watabou.pixeldungeon.items.crafting.EyeOfEnder
import com.watabou.pixeldungeon.items.crafting.Bone
import com.watabou.pixeldungeon.items.crafting.Hoe
import com.watabou.pixeldungeon.items.crafting.TreeSapling
import com.watabou.pixeldungeon.items.crafting.WoodPlank
import com.watabou.pixeldungeon.items.crafting.WoodBarricadeItem
import com.watabou.pixeldungeon.items.crafting.WoodenAxe
import com.watabou.pixeldungeon.items.crafting.WoodenPickaxe
import com.watabou.pixeldungeon.items.crafting.StonePickaxe
import com.watabou.pixeldungeon.items.crafting.IronPickaxe
import com.watabou.pixeldungeon.items.crafting.DiamondPickaxe
import com.watabou.pixeldungeon.items.crafting.SpikeTrapItem
import com.watabou.pixeldungeon.items.crafting.TorchHolderItem
import com.watabou.pixeldungeon.items.crafting.SupportBeamItem
import com.watabou.pixeldungeon.items.crafting.SafeRoomBlueprint
import com.watabou.pixeldungeon.items.crafting.MiniForgeItem
import com.watabou.pixeldungeon.items.crafting.ResourceCacheItem
import com.watabou.pixeldungeon.items.weapon.melee.crafted.StoneAxe
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.food.farming.PlanterBox
import com.watabou.pixeldungeon.farming.WheatSeed
import com.watabou.pixeldungeon.farming.CarrotSeed
import com.watabou.pixeldungeon.farming.PotatoSeed
import com.watabou.pixeldungeon.farming.MelonSeed
import com.watabou.pixeldungeon.items.quest.DarkGold
import com.watabou.pixeldungeon.items.food.CheeseWedge
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.items.scrolls.ScrollOfIdentify
import com.watabou.pixeldungeon.items.scrolls.ScrollOfMagicMapping
import com.watabou.pixeldungeon.items.weapon.melee.Knuckles
import com.watabou.pixeldungeon.items.weapon.melee.ShortSword
import com.watabou.pixeldungeon.items.weapon.missiles.Dart
import com.watabou.pixeldungeon.levels.painters.Painter
import com.watabou.pixeldungeon.plants.Brightcap
import com.watabou.pixeldungeon.plants.Sungrass
import com.watabou.utils.ColorMath
import com.watabou.utils.Random
import java.util.Arrays

class VillageLevel : Level() {

    override val levelWidth = 128
    override val levelHeight = 128

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }

    override fun tilesTex(): String = Assets.TILES_VILLAGE

    override fun waterTex(): String = Assets.WATER_VILLAGE

    override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)

        // === Large central open area (grass) ===
        Painter.fill(this, 5, 5, 54, 54, Terrain.GRASS)

        // === Extended grass for forest zone (east portion) ===
        Painter.fill(this, 59, 5, 66, 60, Terrain.GRASS)

        // === Main paths (cobblestone) ===
        // Horizontal path through center
        Painter.fill(this, 5, 22, 54, 2, Terrain.EMPTY_DECO)
        // Vertical path through center
        Painter.fill(this, 32, 5, 2, 54, Terrain.EMPTY_DECO)
        // Path to entrance
        Painter.fill(this, 32, 2, 2, 3, Terrain.EMPTY_DECO)
        // Path to exit
        Painter.fill(this, 32, 59, 2, 3, Terrain.EMPTY_DECO)

        // ============================================================
        // VILLAGE SQUARE (center-north, shops + elder + well)
        // ============================================================

        // === Weapon Shop (northwest) ===
        Painter.fill(this, 8, 6, 11, 10, Terrain.WALL)
        Painter.fill(this, 9, 7, 9, 8, Terrain.EMPTY_SP)
        map[pos(14, 15)] = Terrain.DOOR
        map[pos(10, 6)] = Terrain.WALL_DECO
        map[pos(16, 6)] = Terrain.WALL_DECO

        // === Potion Shop (northeast) ===
        Painter.fill(this, 38, 6, 11, 10, Terrain.WALL)
        Painter.fill(this, 39, 7, 9, 8, Terrain.EMPTY_SP)
        map[pos(43, 15)] = Terrain.DOOR
        map[pos(40, 6)] = Terrain.WALL_DECO
        map[pos(46, 6)] = Terrain.WALL_DECO

        // === Tavern (center-west) ===
        Painter.fill(this, 8, 14, 11, 8, Terrain.WALL)
        Painter.fill(this, 9, 15, 9, 6, Terrain.EMPTY_SP)
        map[pos(14, 21)] = Terrain.DOOR
        map[pos(10, 21)] = Terrain.WALL_DECO
        map[pos(16, 21)] = Terrain.WALL_DECO

        // === Central square decorations ===
        map[pos(33, 18)] = Terrain.WELL
        map[pos(30, 18)] = Terrain.SIGN
        map[pos(28, 20)] = Terrain.EMBERS

        // === Village Garden (east of center) ===
        Painter.fill(this, 38, 17, 10, 5, Terrain.HIGH_GRASS)
        Painter.fill(this, 39, 18, 8, 3, Terrain.GRASS)
        map[pos(38, 19)] = Terrain.STATUE
        map[pos(47, 19)] = Terrain.STATUE

        // === Herbalist's Corner (west of center) ===
        Painter.fill(this, 22, 17, 6, 3, Terrain.EMPTY_SP)
        map[pos(22, 17)] = Terrain.BOOKSHELF
        map[pos(27, 19)] = Terrain.BOOKSHELF
        map[pos(24, 18)] = Terrain.ALCHEMY

        // === Hidden Stash (behind weapon shop north wall) ===
        Painter.fill(this, 13, 5, 3, 1, Terrain.EMPTY_SP)
        map[pos(14, 6)] = Terrain.SECRET_DOOR

        // ============================================================
        // WORKSHOP ZONE (southwest, ~5-30, 25-45)
        // ============================================================

        // === Workshop building ===
        Painter.fill(this, 8, 26, 14, 12, Terrain.WALL)
        Painter.fill(this, 9, 27, 12, 10, Terrain.EMPTY_SP)
        map[pos(15, 26)] = Terrain.DOOR
        map[pos(10, 37)] = Terrain.WALL_DECO
        map[pos(18, 37)] = Terrain.WALL_DECO
        map[pos(10, 28)] = Terrain.CRAFTING_TABLE
        map[pos(18, 28)] = Terrain.FURNACE
        map[pos(14, 30)] = Terrain.EMBERS    // forge fire
        map[pos(10, 32)] = Terrain.ENCHANTING_TABLE
        map[pos(18, 32)] = Terrain.ANVIL

        // ============================================================
        // FARMING ZONE (southwest, ~5-30, 46-58)
        // ============================================================

        // Farmland area
        Painter.fill(this, 8, 48, 10, 4, Terrain.FARMLAND)
        // Some hydrated near a water source
        Painter.fill(this, 8, 48, 3, 2, Terrain.HYDRATED_FARMLAND)
        // Small pond for hydration
        Painter.fill(this, 6, 47, 2, 2, Terrain.WATER)

        // ============================================================
        // MINING ZONE (southeast, ~40-58, 25-50)
        // ============================================================

        // Sign for mining zone
        map[pos(42, 25)] = Terrain.SIGN

        // Dirt wall section (5x3)
        Painter.fill(this, 42, 27, 5, 3, Terrain.DIRT_WALL)
        map[pos(42, 26)] = Terrain.SIGN  // label

        // Stone wall section (5x3)
        Painter.fill(this, 42, 31, 5, 3, Terrain.STONE_WALL_NATURAL)
        map[pos(42, 30)] = Terrain.SIGN

        // Granite wall section (5x3)
        Painter.fill(this, 42, 35, 5, 3, Terrain.GRANITE_WALL)
        map[pos(42, 34)] = Terrain.SIGN

        // Obsidian wall section (5x3)
        Painter.fill(this, 42, 39, 5, 3, Terrain.OBSIDIAN_WALL)
        map[pos(42, 38)] = Terrain.SIGN

        // Ore wall samples (2 cells each)
        Painter.fill(this, 49, 27, 2, 1, Terrain.ORE_WALL_IRON)
        Painter.fill(this, 49, 29, 2, 1, Terrain.ORE_WALL_GOLD)
        Painter.fill(this, 49, 31, 2, 1, Terrain.ORE_WALL_DIAMOND)
        Painter.fill(this, 49, 33, 2, 1, Terrain.ORE_WALL_ARCANE)

        // Clear paths around mining walls so they're accessible
        Painter.fill(this, 41, 26, 1, 17, Terrain.EMPTY_DECO)  // left path
        Painter.fill(this, 47, 26, 1, 17, Terrain.EMPTY_DECO)  // middle path
        Painter.fill(this, 51, 26, 1, 9, Terrain.EMPTY_DECO)   // right ore path
        Painter.fill(this, 48, 26, 1, 9, Terrain.EMPTY_DECO)   // ore access
        // Horizontal paths between wall sections
        Painter.fill(this, 41, 30, 7, 1, Terrain.EMPTY_DECO)
        Painter.fill(this, 41, 34, 7, 1, Terrain.EMPTY_DECO)
        Painter.fill(this, 41, 38, 7, 1, Terrain.EMPTY_DECO)
        Painter.fill(this, 41, 42, 7, 1, Terrain.EMPTY_DECO)

        // ============================================================
        // BUILDING ZONE (southeast, ~40-58, 51-58)
        // ============================================================

        // Open 10x10 area for placing fortifications
        Painter.fill(this, 42, 48, 12, 10, Terrain.EMPTY)
        map[pos(42, 47)] = Terrain.SIGN  // label

        // ============================================================
        // HIGH GRASS / HEDGES on corners
        // ============================================================
        Painter.fill(this, 5, 5, 3, 3, Terrain.HIGH_GRASS)
        Painter.fill(this, 56, 5, 3, 3, Terrain.HIGH_GRASS)
        Painter.fill(this, 56, 56, 3, 3, Terrain.HIGH_GRASS)
        Painter.fill(this, 5, 56, 3, 3, Terrain.HIGH_GRASS)

        // ============================================================
        // FOREST ZONE (east portion, ~80-120, 10-60)
        // ============================================================

        // Path connecting village to forest zone
        Painter.fill(this, 58, 22, 22, 2, Terrain.EMPTY_DECO)

        buildForestZone()

        // Scatter some high grass
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.GRASS && Random.Int(12) == 0) {
                map[i] = Terrain.HIGH_GRASS
            }
        }

        // === Entrance (north center) ===
        entrance = pos(33, 2)
        map[entrance] = Terrain.ENTRANCE
        map[pos(33, 3)] = Terrain.EMPTY_DECO

        // === Exit (south center) ===
        exit = pos(33, 61)
        map[exit] = Terrain.EXIT
        map[pos(33, 60)] = Terrain.EMPTY_DECO

        feeling = Feeling.NONE

        return true
    }

    override fun decorate() {
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.GRASS && Random.Int(20) == 0) {
                map[i] = Terrain.EMPTY_DECO
            }
        }
    }

    override fun createMobs() {
        // 3 Shopkeepers
        placeShopkeeper(pos(14, 10))   // Weapon shop
        placeShopkeeper(pos(43, 10))   // Potion shop
        placeShopkeeper(pos(14, 18))   // Tavern

        // Village Elder in the central square
        val elder = VillageElder()
        elder.pos = pos(31, 18)
        mobs.add(elder)
        Actor.occupyCell(elder)

        // 1-2 rats on the outskirts
        val rat1 = Rat()
        rat1.pos = pos(55, 55)
        mobs.add(rat1)
        Actor.occupyCell(rat1)

        if (Random.Int(2) == 0) {
            val rat2 = Rat()
            rat2.pos = pos(7, 40)
            mobs.add(rat2)
            Actor.occupyCell(rat2)
        }
    }

    private fun placeShopkeeper(cell: Int) {
        val shopkeeper = Shopkeeper()
        shopkeeper.pos = cell
        mobs.add(shopkeeper)
        Actor.occupyCell(shopkeeper)
    }

    override fun createItems() {
        // === Starter gold near the entrance ===
        drop(Gold(500), pos(32, 4))

        // === Healing well ===
        val wellCell = pos(33, 18)
        val water = WaterOfHealth()
        water.seed(wellCell, 1)
        blobs[WaterOfHealth::class.java] = water

        // === Weapon Shop inventory ===
        placeForSale(ShortSword().identify(), pos(10, 8))
        placeForSale(Knuckles().identify(), pos(12, 8))
        placeForSale(ClothArmor().identify(), pos(14, 8))
        placeForSale(LeatherArmor().identify(), pos(16, 8))
        placeForSale(Dart(3), pos(10, 12))

        // === Potion Shop inventory ===
        placeForSale(PotionOfHealing(), pos(40, 8))
        placeForSale(Generator.random(Generator.Category.POTION) ?: PotionOfHealing(), pos(42, 8))
        placeForSale(Generator.random(Generator.Category.POTION) ?: PotionOfHealing(), pos(44, 8))
        placeForSale(ScrollOfIdentify(), pos(40, 12))
        placeForSale(ScrollOfMagicMapping(), pos(42, 12))
        placeForSale(SeedPouch(), pos(44, 12))

        // === Tavern inventory ===
        placeForSale(Food(), pos(10, 16))
        placeForSale(Food(), pos(12, 16))
        placeForSale(CheeseWedge(), pos(14, 16))
        placeForSale(Torch(), pos(10, 19))
        placeForSale(Weightstone(), pos(12, 19))

        // === Village Garden — Foliage blob + plants ===
        val foliage = Foliage()
        for (gy in 17..21) {
            for (gx in 38..47) {
                foliage.seed(pos(gx, gy), 1)
            }
        }
        blobs[Foliage::class.java] = foliage
        plant(Sungrass.Seed(), pos(42, 19))
        plant(Brightcap.Seed(), pos(44, 19))

        // === Herbalist's Alchemy pot ===
        val alchemy = Alchemy()
        alchemy.seed(pos(24, 18), 1)
        blobs[Alchemy::class.java] = alchemy

        // === Hidden Stash ===
        val stashItem: Item = when (Random.Int(4)) {
            0 -> Honeypot()
            1 -> Ankh()
            2 -> HolyWater()
            else -> SmokeBomb().apply { quantity = 2 }
        }
        drop(stashItem, pos(14, 5)).type = Heap.Type.CHEST

        // === Workshop inventory ===
        placeForSale(MaterialBag(), pos(14, 28))

        // === TEST MATERIALS (Workshop) ===
        // Furnace inputs (near furnace at 18,28)
        drop(IronOre().apply { quantity = 5 }, pos(17, 27))
        drop(GoldOre().apply { quantity = 3 }, pos(19, 27))
        drop(MysteryMeat().apply { quantity = 3 }, pos(19, 29))
        drop(DarkGold().apply { quantity = 3 }, pos(19, 30))
        drop(Cobblestone().apply { quantity = 15 }, pos(17, 29))
        // Crafting table inputs (near table at 10,28)
        drop(Stick().apply { quantity = 15 }, pos(9, 27))
        drop(Fiber().apply { quantity = 8 }, pos(11, 27))
        drop(Leather().apply { quantity = 8 }, pos(12, 27))
        drop(DiamondShard().apply { quantity = 5 }, pos(13, 27))
        drop(IronIngot().apply { quantity = 15 }, pos(9, 29))
        drop(WoodPlank().apply { quantity = 12 }, pos(11, 29))
        // Enchanting + anvil test items
        drop(ArcaneDust().apply { quantity = 50 }, pos(9, 32))
        drop(BlankTome(), pos(12, 32))
        drop(ArcaneDust().apply { quantity = 50 }, pos(9, 34))
        drop(ScrollOfIdentify(), pos(12, 34))
        drop(ScrollOfMagicMapping(), pos(13, 34))
        drop(ShortSword().identify(), pos(19, 33))
        // Storage chest materials
        drop(WoodPlank().apply { quantity = 16 }, pos(13, 29))
        drop(EyeOfEnder().apply { quantity = 2 }, pos(9, 31))

        // === FARMING ZONE TEST ITEMS ===
        drop(Hoe(), pos(8, 47))
        drop(WheatSeed().apply { quantity = 5 }, pos(9, 47))
        drop(CarrotSeed().apply { quantity = 5 }, pos(10, 47))
        drop(PotatoSeed().apply { quantity = 5 }, pos(11, 47))
        drop(MelonSeed().apply { quantity = 3 }, pos(12, 47))
        drop(PlanterBox(), pos(13, 47))
        drop(Bone().apply { quantity = 9 }, pos(14, 47))

        // === MINING ZONE TEST ITEMS ===
        // All 4 pickaxe tiers
        drop(WoodenPickaxe(), pos(41, 27))
        drop(StonePickaxe(), pos(41, 28))
        drop(IronPickaxe(), pos(41, 29))
        drop(DiamondPickaxe(), pos(41, 30))
        // Extra crafting materials for mining
        drop(WoodPlank().apply { quantity = 12 }, pos(48, 27))
        drop(Stick().apply { quantity = 10 }, pos(48, 28))
        drop(IronIngot().apply { quantity = 10 }, pos(48, 29))
        drop(DiamondShard().apply { quantity = 6 }, pos(48, 30))
        drop(ArcaneOre().apply { quantity = 3 }, pos(48, 31))

        // === BUILDING ZONE TEST ITEMS ===
        drop(CobblestoneBlock().apply { quantity = 10 }, pos(42, 49))
        drop(WoodBarricadeItem().apply { quantity = 5 }, pos(43, 49))
        drop(SpikeTrapItem().apply { quantity = 5 }, pos(44, 49))
        drop(TorchHolderItem().apply { quantity = 3 }, pos(45, 49))
        drop(SupportBeamItem().apply { quantity = 3 }, pos(46, 49))
        drop(SafeRoomBlueprint(), pos(47, 49))
        drop(MiniForgeItem(), pos(48, 49))
        drop(ResourceCacheItem().apply { quantity = 2 }, pos(49, 49))

        // === FOREST ZONE TEST ITEMS (near lumber yard) ===
        // All 4 axe tiers
        drop(WoodenAxe(), pos(75, 21))
        drop(StoneAxe(), pos(76, 21))
        drop(IronAxe(), pos(77, 21))
        drop(DiamondAxe(), pos(78, 21))
        // Wood materials
        drop(Log().apply { quantity = 10 }, pos(75, 23))
        drop(Bark().apply { quantity = 8 }, pos(76, 23))
        drop(Resin().apply { quantity = 5 }, pos(77, 23))
        // Tree saplings for replanting
        drop(TreeSapling().apply { quantity = 5 }, pos(78, 23))
        drop(TreeSapling().apply { quantity = 3 }, pos(79, 23))
        // Tree stumps for testing replanting (placed in build, but terrain set here)
        map[pos(75, 26)] = Terrain.TREE_STUMP
        map[pos(77, 26)] = Terrain.TREE_STUMP

        // Record village in journal
        Journal.add(Journal.Feature.VILLAGE)
    }

    private fun placeForSale(item: Item, cell: Int) {
        drop(item, cell).type = Heap.Type.FOR_SALE
    }

    override fun addItemToSpawn(item: Item?) {
        // No-op: prevent PotionOfStrength/ScrollOfUpgrade from being added
    }

    override fun respawner(): Actor? = null

    override fun nMobs(): Int = 0

    override fun randomRespawnCell(): Int = -1

    override fun tileName(tile: Int): String {
        return when (tile) {
            Terrain.WATER -> "Pond"
            Terrain.HIGH_GRASS -> "Hedge"
            Terrain.WALL_DECO -> "Window"
            Terrain.EMPTY_DECO -> "Cobblestone path"
            Terrain.EMPTY_SP -> "Wooden floor"
            Terrain.GRASS -> "Village green"
            Terrain.EMBERS -> "Campfire"
            Terrain.WELL -> "Healing well"
            Terrain.EMPTY_WELL -> "Dried up well"
            Terrain.SIGN -> "Signpost"
            Terrain.ENTRANCE -> "Village gate"
            Terrain.EXIT -> "Dungeon entrance"
            Terrain.DOOR -> "Wooden door"
            Terrain.OPEN_DOOR -> "Open door"
            Terrain.WALL -> "Building wall"
            Terrain.STATUE -> "Garden statue"
            Terrain.BOOKSHELF -> "Herbalist's shelf"
            Terrain.ALCHEMY -> "Alchemy pot"
            Terrain.CRAFTING_TABLE -> "Crafting table"
            Terrain.FURNACE -> "Furnace"
            Terrain.FARMLAND -> "Farmland"
            Terrain.HYDRATED_FARMLAND -> "Hydrated farmland"
            Terrain.DIRT_WALL -> "Dirt wall (test - mine with Wood Pickaxe)"
            Terrain.STONE_WALL_NATURAL -> "Stone wall (test - mine with Stone Pickaxe)"
            Terrain.GRANITE_WALL -> "Granite wall (test - mine with Iron Pickaxe)"
            Terrain.OBSIDIAN_WALL -> "Obsidian wall (test - mine with Diamond Pickaxe)"
            Terrain.ORE_WALL_IRON -> "Iron ore vein (test)"
            Terrain.ORE_WALL_GOLD -> "Gold ore vein (test)"
            Terrain.ORE_WALL_DIAMOND -> "Diamond ore vein (test)"
            Terrain.ORE_WALL_ARCANE -> "Arcane ore vein (test)"
            Terrain.TREE_OAK -> "Oak tree"
            Terrain.TREE_BIRCH -> "Birch tree"
            Terrain.TREE_PINE -> "Pine tree"
            Terrain.TREE_MAPLE -> "Maple tree"
            Terrain.TREE_WILLOW -> "Willow tree"
            Terrain.TREE_FRUIT -> "Fruit tree"
            Terrain.TREE_STUMP -> "Tree stump"
            else -> super.tileName(tile)
        }
    }

    override fun tileDesc(tile: Int): String {
        return when (tile) {
            Terrain.ENTRANCE -> "A sturdy gate marks the edge of the village. Beyond lies the open world."
            Terrain.EXIT -> "Stone steps descend into darkness. The dungeon awaits below."
            Terrain.WELL -> "A stone well filled with healing waters. Drink deep to restore your strength."
            Terrain.EMPTY_WELL -> "The well has run dry."
            Terrain.SIGN -> "A weathered signpost reads: 'Prepare well, adventurer. The dungeon shows no mercy.'"
            Terrain.EMBERS -> "A crackling campfire warms the village square."
            Terrain.EMPTY_SP -> "Sturdy wooden planks form the floor of this building."
            Terrain.WALL_DECO -> "A small window lets light into the building."
            Terrain.STATUE -> "A moss-covered stone figure watches over the village garden."
            Terrain.BOOKSHELF -> "Shelves lined with dried herbs and botanical references."
            Terrain.ALCHEMY -> "A sturdy cauldron for brewing potions from seeds."
            Terrain.CRAFTING_TABLE -> "A sturdy workbench for crafting weapons, armor, and tools."
            Terrain.FURNACE -> "A hot furnace for smelting ores into metal ingots."
            Terrain.FARMLAND -> "Tilled earth ready for planting. Use a hoe on grass to create more."
            Terrain.HYDRATED_FARMLAND -> "Moist, tilled earth near water. Crops grow faster here."
            Terrain.DIRT_WALL -> "Packed earth. Mine with a Wooden Pickaxe or better."
            Terrain.STONE_WALL_NATURAL -> "Solid stone. Mine with a Stone Pickaxe or better."
            Terrain.GRANITE_WALL -> "Dense granite. Mine with an Iron Pickaxe or better."
            Terrain.OBSIDIAN_WALL -> "Volcanic obsidian. Only a Diamond Pickaxe can break this."
            Terrain.TREE_OAK -> "A sturdy oak tree with a wide canopy. Chop it with an axe for wood."
            Terrain.TREE_BIRCH -> "A slender birch tree with pale bark. Easy to chop."
            Terrain.TREE_PINE -> "A tall pine tree with dark green needles. Hard wood, may yield resin."
            Terrain.TREE_MAPLE -> "A maple tree with broad leaves. Good source of quality wood."
            Terrain.TREE_WILLOW -> "A graceful willow tree with drooping branches. Often found near water."
            Terrain.TREE_FRUIT -> "A fruit tree laden with ripe fruit. Chop for wood and a harvest."
            Terrain.TREE_STUMP -> "The remains of a felled tree. A sapling could be planted here."
            else -> super.tileDesc(tile)
        }
    }

    override fun addVisuals(scene: Scene) {
        for (i in 0 until LENGTH) {
            when (map[i]) {
                Terrain.EMBERS -> {
                    scene.add(Campfire(i))
                    scene.add(CampfireSmoke(i))
                }
                Terrain.WATER -> if (Random.Int(3) == 0) scene.add(PondSparkle(i))
                Terrain.GRASS -> if (Random.Int(8) == 0) scene.add(WindParticle.Wind(i))
                Terrain.HIGH_GRASS -> if (Random.Int(6) == 0) scene.add(VillageLeaf(i))
            }
        }
    }

    private fun buildForestZone() {
        // === Large grass base for the forest ===
        Painter.fill(this, 80, 10, 42, 52, Terrain.GRASS)

        // === Lumber Yard (near forest entrance, ~75-80, 20-30) ===
        Painter.fill(this, 75, 20, 6, 10, Terrain.EMPTY_SP)
        map[pos(77, 19)] = Terrain.SIGN  // Lumber yard sign

        // === Oak Grove (6-8 trees, ~85-95, 15-25) ===
        map[pos(86, 16)] = Terrain.TREE_OAK
        map[pos(89, 17)] = Terrain.TREE_OAK
        map[pos(92, 16)] = Terrain.TREE_OAK
        map[pos(87, 20)] = Terrain.TREE_OAK
        map[pos(90, 19)] = Terrain.TREE_OAK
        map[pos(93, 21)] = Terrain.TREE_OAK
        map[pos(88, 23)] = Terrain.TREE_OAK
        map[pos(91, 24)] = Terrain.TREE_OAK

        // === Birch Copse (4-6 trees, ~100-110, 15-25) ===
        map[pos(101, 16)] = Terrain.TREE_BIRCH
        map[pos(104, 17)] = Terrain.TREE_BIRCH
        map[pos(107, 16)] = Terrain.TREE_BIRCH
        map[pos(102, 20)] = Terrain.TREE_BIRCH
        map[pos(105, 21)] = Terrain.TREE_BIRCH
        map[pos(108, 19)] = Terrain.TREE_BIRCH

        // === Pine Stand (4-5 trees, ~85-95, 30-40) ===
        map[pos(86, 31)] = Terrain.TREE_PINE
        map[pos(89, 33)] = Terrain.TREE_PINE
        map[pos(92, 32)] = Terrain.TREE_PINE
        map[pos(87, 36)] = Terrain.TREE_PINE
        map[pos(91, 38)] = Terrain.TREE_PINE

        // === Maple Cluster (3-4 trees, ~100-110, 30-40) ===
        map[pos(101, 31)] = Terrain.TREE_MAPLE
        map[pos(104, 33)] = Terrain.TREE_MAPLE
        map[pos(107, 35)] = Terrain.TREE_MAPLE
        map[pos(103, 37)] = Terrain.TREE_MAPLE

        // === Willow Area (3-4 trees near a small pond, ~85-95, 45-55) ===
        // Small pond for willows
        Painter.fill(this, 88, 48, 4, 3, Terrain.WATER)
        map[pos(86, 46)] = Terrain.TREE_WILLOW
        map[pos(89, 45)] = Terrain.TREE_WILLOW
        map[pos(93, 47)] = Terrain.TREE_WILLOW
        map[pos(85, 50)] = Terrain.TREE_WILLOW

        // === Fruit Orchard (4-5 trees, ~100-110, 45-55) ===
        map[pos(101, 46)] = Terrain.TREE_FRUIT
        map[pos(104, 47)] = Terrain.TREE_FRUIT
        map[pos(107, 46)] = Terrain.TREE_FRUIT
        map[pos(102, 51)] = Terrain.TREE_FRUIT
        map[pos(106, 52)] = Terrain.TREE_FRUIT

        // === Paths through the forest ===
        // Main east-west path connecting lumber yard to deep forest
        Painter.fill(this, 80, 22, 40, 1, Terrain.EMPTY_DECO)
        // North-south path through tree clusters
        Painter.fill(this, 98, 12, 1, 48, Terrain.EMPTY_DECO)
    }

    private fun pos(x: Int, y: Int): Int = x + y * WIDTH

    // Campfire particle emitter
    private class Campfire(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 2, p.y - 2, 4f, 4f)
            pour(FlameParticle.FACTORY, 0.1f)
        }

        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }
    }

    // Leaf particles drifting from hedges
    private class VillageLeaf(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 4, p.y - 4, 8f, 8f)
            pour(LeafParticle.LEVEL_SPECIFIC, 0.8f)
        }

        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }
    }

    // Smoke wisps rising above the campfire
    private class CampfireSmoke(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x - 2, p.y - 6, 4f, 4f)
            pour(SmokeParticle.FACTORY, 0.4f)
        }

        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }
    }

    // Gentle water sparkle on pond tiles
    private class PondSparkle(private val pos: Int) : Emitter() {
        init {
            val p = DungeonTilemap.tileCenterToWorld(pos)
            pos(p.x, p.y, 0f, 0f)
            pour(sparkleFactory, 0.6f)
        }

        override fun update() {
            visible = Dungeon.visible[pos]
            if (visible) {
                super.update()
            }
        }

        companion object {
            private val sparkleFactory: Factory = object : Factory() {
                override fun emit(emitter: Emitter, index: Int, x: Float, y: Float) {
                    val p = emitter.recycle(Sparkle::class.java) as Sparkle
                    p.reset(x, y)
                }
            }
        }
    }

    class Sparkle : PixelParticle() {
        init {
            color(ColorMath.random(0x60a4c8, 0x80c8e8))
            lifespan = 1.0f
            acc.set(0f, -10f)
        }

        fun reset(x: Float, y: Float) {
            revive()
            this.x = x + Random.Float(-4f, 4f)
            this.y = y + Random.Float(-4f, 4f)
            left = lifespan
            size(2f)
            speed.set(0f)
        }

        override fun update() {
            super.update()
            val p = left / lifespan
            am = (if (p < 0.5f) p else 1 - p) * 0.4f
        }
    }
}
