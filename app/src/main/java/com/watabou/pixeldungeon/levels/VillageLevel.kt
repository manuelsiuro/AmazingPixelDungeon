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
import com.watabou.pixeldungeon.items.crafting.Cobblestone
import com.watabou.pixeldungeon.items.crafting.DiamondShard
import com.watabou.pixeldungeon.items.crafting.Fiber
import com.watabou.pixeldungeon.items.crafting.GoldOre
import com.watabou.pixeldungeon.items.crafting.IronIngot
import com.watabou.pixeldungeon.items.crafting.IronOre
import com.watabou.pixeldungeon.items.crafting.Leather
import com.watabou.pixeldungeon.items.crafting.Stick
import com.watabou.pixeldungeon.items.crafting.ArcaneDust
import com.watabou.pixeldungeon.items.crafting.BlankTome
import com.watabou.pixeldungeon.items.crafting.EyeOfEnder
import com.watabou.pixeldungeon.items.crafting.Bone
import com.watabou.pixeldungeon.items.crafting.Hoe
import com.watabou.pixeldungeon.items.crafting.WoodPlank
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

    init {
        color1 = 0x48763c
        color2 = 0x59994a
    }

    override fun tilesTex(): String = Assets.TILES_VILLAGE

    override fun waterTex(): String = Assets.WATER_VILLAGE

    override fun build(): Boolean {
        Arrays.fill(map, Terrain.WALL)

        // Central open area (grass and paths)
        Painter.fill(this, 3, 3, 26, 26, Terrain.GRASS)

        // Main paths (cobblestone)
        // Horizontal path through center
        Painter.fill(this, 3, 15, 26, 2, Terrain.EMPTY_DECO)
        // Vertical path through center
        Painter.fill(this, 15, 3, 2, 26, Terrain.EMPTY_DECO)

        // === Weapon Shop (northwest) ===
        Painter.fill(this, 4, 4, 9, 8, Terrain.WALL)
        Painter.fill(this, 5, 5, 7, 6, Terrain.EMPTY_SP)
        map[pos(8, 11)] = Terrain.DOOR
        map[pos(6, 4)] = Terrain.WALL_DECO
        map[pos(10, 4)] = Terrain.WALL_DECO

        // === Potion Shop (northeast) ===
        Painter.fill(this, 19, 4, 9, 8, Terrain.WALL)
        Painter.fill(this, 20, 5, 7, 6, Terrain.EMPTY_SP)
        map[pos(23, 11)] = Terrain.DOOR
        map[pos(21, 4)] = Terrain.WALL_DECO
        map[pos(25, 4)] = Terrain.WALL_DECO

        // === Tavern (south-west) ===
        Painter.fill(this, 4, 20, 9, 8, Terrain.WALL)
        Painter.fill(this, 5, 21, 7, 6, Terrain.EMPTY_SP)
        map[pos(8, 20)] = Terrain.DOOR
        map[pos(6, 27)] = Terrain.WALL_DECO
        map[pos(10, 27)] = Terrain.WALL_DECO

        // === Central square decorations ===
        map[pos(16, 16)] = Terrain.WELL
        map[pos(14, 14)] = Terrain.SIGN
        map[pos(10, 18)] = Terrain.EMBERS

        // === Village Garden (east of center, north of pond) ===
        Painter.fill(this, 20, 17, 7, 4, Terrain.HIGH_GRASS)
        Painter.fill(this, 21, 18, 5, 2, Terrain.GRASS)
        map[pos(20, 18)] = Terrain.STATUE  // garden entrance statue left
        map[pos(26, 18)] = Terrain.STATUE  // garden entrance statue right

        // === Farmland area (south of garden, near seed drops) ===
        Painter.fill(this, 20, 13, 6, 2, Terrain.FARMLAND)
        // Tiles closer to the well (16,16) get hydrated
        map[pos(20, 13)] = Terrain.HYDRATED_FARMLAND
        map[pos(20, 14)] = Terrain.HYDRATED_FARMLAND
        map[pos(21, 13)] = Terrain.HYDRATED_FARMLAND

        // === Herbalist's Corner (west of center) ===
        Painter.fill(this, 5, 13, 5, 2, Terrain.EMPTY_SP)
        map[pos(5, 13)] = Terrain.BOOKSHELF   // herbalist shelf left
        map[pos(9, 14)] = Terrain.BOOKSHELF   // herbalist shelf right
        map[pos(7, 13)] = Terrain.ALCHEMY     // alchemy pot

        // === Hidden Stash (behind weapon shop north wall) ===
        Painter.fill(this, 7, 3, 3, 1, Terrain.EMPTY_SP)  // alcove
        map[pos(8, 4)] = Terrain.SECRET_DOOR               // secret door in north wall

        // === Workshop (southeast) ===
        Painter.fill(this, 19, 20, 9, 8, Terrain.WALL)
        Painter.fill(this, 20, 21, 7, 6, Terrain.EMPTY_SP)
        map[pos(23, 20)] = Terrain.DOOR           // north door facing path
        map[pos(21, 27)] = Terrain.WALL_DECO      // south window left
        map[pos(25, 27)] = Terrain.WALL_DECO      // south window right
        map[pos(21, 22)] = Terrain.CRAFTING_TABLE  // craft table
        map[pos(25, 22)] = Terrain.FURNACE         // furnace
        map[pos(23, 24)] = Terrain.EMBERS          // forge fire
        map[pos(21, 25)] = Terrain.ENCHANTING_TABLE  // enchanting table
        map[pos(25, 25)] = Terrain.ANVIL             // anvil

        // === High grass / hedges on edges ===
        Painter.fill(this, 3, 3, 2, 2, Terrain.HIGH_GRASS)
        Painter.fill(this, 27, 3, 2, 2, Terrain.HIGH_GRASS)
        Painter.fill(this, 27, 27, 2, 2, Terrain.HIGH_GRASS)
        Painter.fill(this, 3, 27, 2, 2, Terrain.HIGH_GRASS)

        // Scatter some high grass
        for (i in 0 until LENGTH) {
            if (map[i] == Terrain.GRASS && Random.Int(12) == 0) {
                map[i] = Terrain.HIGH_GRASS
            }
        }

        // === Entrance (north edge) - gate to the outside world ===
        entrance = pos(16, 2)
        map[entrance] = Terrain.ENTRANCE
        map[pos(16, 3)] = Terrain.EMPTY_DECO

        // === Exit (south center) - stairs down to dungeon ===
        exit = pos(16, 29)
        map[exit] = Terrain.EXIT
        map[pos(16, 28)] = Terrain.EMPTY_DECO

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
        placeShopkeeper(pos(8, 7))   // Weapon shop
        placeShopkeeper(pos(23, 7))  // Potion shop
        placeShopkeeper(pos(8, 24))  // Tavern

        // Village Elder in the central square
        val elder = VillageElder()
        elder.pos = pos(15, 14)
        mobs.add(elder)
        Actor.occupyCell(elder)

        // 1-2 rats on the outskirts
        val rat1 = Rat()
        rat1.pos = pos(26, 28)
        mobs.add(rat1)
        Actor.occupyCell(rat1)

        if (Random.Int(2) == 0) {
            val rat2 = Rat()
            rat2.pos = pos(4, 16)
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
        drop(Gold(500), pos(15, 3))

        // === Healing well (WaterOfHealth on the well tile) ===
        val wellCell = pos(16, 16)
        val water = WaterOfHealth()
        water.seed(wellCell, 1)
        blobs[WaterOfHealth::class.java] = water

        // === Weapon Shop inventory ===
        placeForSale(ShortSword().identify(), pos(6, 6))
        placeForSale(Knuckles().identify(), pos(7, 6))
        placeForSale(ClothArmor().identify(), pos(9, 6))
        placeForSale(LeatherArmor().identify(), pos(10, 6))
        placeForSale(Dart(3), pos(6, 8))

        // === Potion Shop inventory ===
        placeForSale(PotionOfHealing(), pos(21, 6))
        placeForSale(Generator.random(Generator.Category.POTION) ?: PotionOfHealing(), pos(22, 6))
        placeForSale(Generator.random(Generator.Category.POTION) ?: PotionOfHealing(), pos(24, 6))
        placeForSale(ScrollOfIdentify(), pos(21, 8))
        placeForSale(ScrollOfMagicMapping(), pos(22, 8))
        placeForSale(SeedPouch(), pos(24, 8))

        // === Tavern inventory ===
        placeForSale(Food(), pos(6, 22))
        placeForSale(Food(), pos(7, 22))
        placeForSale(CheeseWedge(), pos(9, 22))
        placeForSale(Torch(), pos(6, 25))
        placeForSale(Weightstone(), pos(7, 25))

        // === Village Garden — Foliage blob + plants ===
        val foliage = Foliage()
        for (gy in 17..20) {
            for (gx in 20..26) {
                foliage.seed(pos(gx, gy), 1)
            }
        }
        blobs[Foliage::class.java] = foliage
        plant(Sungrass.Seed(), pos(23, 19))
        plant(Brightcap.Seed(), pos(21, 19))

        // === Herbalist's Alchemy pot ===
        val alchemy = Alchemy()
        alchemy.seed(pos(7, 13), 1)
        blobs[Alchemy::class.java] = alchemy

        // === Hidden Stash (chest behind weapon shop) ===
        val stashItem: Item = when (Random.Int(4)) {
            0 -> Honeypot()
            1 -> Ankh()
            2 -> HolyWater()
            else -> SmokeBomb().apply { quantity = 2 }
        }
        drop(stashItem, pos(8, 3)).type = Heap.Type.CHEST

        // === Workshop inventory ===
        placeForSale(MaterialBag(), pos(23, 22))

        // === TEST MATERIALS — remove after testing ===
        // Furnace inputs (near furnace at 25,22)
        drop(IronOre().apply { quantity = 5 }, pos(24, 21))
        drop(GoldOre().apply { quantity = 3 }, pos(25, 21))
        drop(MysteryMeat().apply { quantity = 3 }, pos(26, 21))
        drop(DarkGold().apply { quantity = 3 }, pos(26, 22))
        // Extra cobblestone for fired blocks (furnace) + crafting table recipes
        drop(Cobblestone().apply { quantity = 15 }, pos(24, 23))
        // Crafting table inputs (near table at 21,22)
        drop(Stick().apply { quantity = 15 }, pos(20, 21))
        drop(Fiber().apply { quantity = 8 }, pos(21, 21))
        drop(Leather().apply { quantity = 8 }, pos(22, 21))
        drop(DiamondShard().apply { quantity = 5 }, pos(23, 21))
        // Pre-smelted ingots for immediate crafting table testing
        drop(IronIngot().apply { quantity = 15 }, pos(20, 23))
        drop(WoodPlank().apply { quantity = 8 }, pos(21, 23))
        // Enchanting table test items (near enchanting table at 21,25)
        drop(ArcaneDust().apply { quantity = 50 }, pos(20, 25))
        drop(BlankTome(), pos(22, 25))
        drop(ArcaneDust().apply { quantity = 50 }, pos(20, 26))
        // Test scrolls for grinding (not for sale — directly dropped)
        drop(ScrollOfIdentify(), pos(22, 26))
        drop(ScrollOfMagicMapping(), pos(23, 26))
        drop(ScrollOfIdentify(), pos(24, 26))
        // Second ShortSword for anvil repair testing
        drop(ShortSword().identify(), pos(26, 25))
        // Storage chest test: extra wood planks + eye of ender for dimensional chest
        drop(WoodPlank().apply { quantity = 16 }, pos(22, 23))
        drop(EyeOfEnder().apply { quantity = 2 }, pos(20, 24))
        // Farming test items (near garden area at 20-26, 15-18)
        drop(Hoe(), pos(20, 15))
        drop(WheatSeed().apply { quantity = 5 }, pos(21, 15))
        drop(CarrotSeed().apply { quantity = 5 }, pos(22, 15))
        drop(PotatoSeed().apply { quantity = 5 }, pos(23, 15))
        drop(MelonSeed().apply { quantity = 3 }, pos(24, 15))
        drop(PlanterBox(), pos(25, 15))
        drop(Bone().apply { quantity = 9 }, pos(26, 15))

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

    private fun pos(x: Int, y: Int): Int = x + y * WIDTH

    // Campfire particle emitter — flame particles rising from embers
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
