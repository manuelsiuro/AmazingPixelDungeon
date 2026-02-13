package com.watabou.pixeldungeon.encyclopedia

import com.watabou.pixeldungeon.Assets
import com.watabou.pixeldungeon.actors.buffs.*
import com.watabou.pixeldungeon.actors.mobs.*
import com.watabou.pixeldungeon.crafting.EnchantmentRegistry
import com.watabou.pixeldungeon.crafting.RecipeRegistry
import com.watabou.pixeldungeon.farming.CropType
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.*
import com.watabou.pixeldungeon.items.armor.crafted.*
import com.watabou.pixeldungeon.items.crafting.*
import com.watabou.pixeldungeon.items.food.*
import com.watabou.pixeldungeon.items.food.farming.*
import com.watabou.pixeldungeon.items.potions.*
import com.watabou.pixeldungeon.items.rings.*
import com.watabou.pixeldungeon.items.scrolls.*
import com.watabou.pixeldungeon.items.wands.*
import com.watabou.pixeldungeon.items.weapon.melee.*
import com.watabou.pixeldungeon.items.weapon.melee.crafted.*
import com.watabou.pixeldungeon.items.weapon.missiles.*
import com.watabou.pixeldungeon.sprites.ItemSpriteSheet
import com.watabou.pixeldungeon.ui.BuffIndicator

object EncyclopediaRegistry {

    private val entries = mutableListOf<EncyclopediaEntry>()
    private var initialized = false

    private fun ensureInitialized() {
        if (initialized) return
        initialized = true
        registerWeapons()
        registerArmor()
        registerPotions()
        registerScrolls()
        registerWands()
        registerRings()
        registerFood()
        registerCrafting()
        registerFarming()
        registerMonsters()
        registerBuffs()
        registerMechanics()
    }

    fun forCategory(cat: EncyclopediaCategory): List<EncyclopediaEntry> {
        ensureInitialized()
        return entries.filter { it.category == cat }
    }

    fun byId(id: String): EncyclopediaEntry? {
        ensureInitialized()
        return entries.find { it.id == id }
    }

    fun subcategoriesFor(cat: EncyclopediaCategory): List<String> {
        return forCategory(cat).map { it.subcategory }.distinct()
    }

    // --- Item registration helper ---

    private fun registerItem(
        cls: Class<out Item>,
        category: EncyclopediaCategory,
        subcategory: String,
        extraStats: Map<String, String> = emptyMap()
    ) {
        try {
            val item = cls.getDeclaredConstructor().newInstance()
            val img = item.image()
            entries.add(EncyclopediaEntry(
                id = cls.simpleName,
                name = item.trueName(),
                category = category,
                iconImage = if (img > 0) img else category.iconImage,
                iconType = IconType.ITEM,
                description = try { item.desc() } catch (e: Exception) { "" },
                stats = extraStats,
                subcategory = subcategory
            ))
        } catch (e: Exception) {
            // Skip uninstantiable items
        }
    }

    private fun registerWithIcon(
        name: String,
        icon: Int,
        cls: Class<out Item>,
        category: EncyclopediaCategory,
        subcategory: String = ""
    ) {
        val desc = try {
            cls.getDeclaredConstructor().newInstance().desc()
        } catch (e: Exception) { "" }
        entries.add(EncyclopediaEntry(
            id = cls.simpleName,
            name = name,
            category = category,
            iconImage = icon,
            iconType = IconType.ITEM,
            description = desc,
            subcategory = subcategory
        ))
    }

    private fun registerItems(
        classes: List<Class<out Item>>,
        category: EncyclopediaCategory,
        subcategory: String
    ) {
        for (cls in classes) {
            registerItem(cls, category, subcategory)
        }
    }

    // --- WEAPONS ---

    private fun registerWeapons() {
        val cat = EncyclopediaCategory.WEAPONS

        // Tier 1
        registerItems(listOf(
            Knuckles::class.java,
            Dagger::class.java,
            ShortSword::class.java
        ), cat, "Tier 1")

        // Tier 2
        registerItems(listOf(
            Quarterstaff::class.java,
            Spear::class.java,
            Mace::class.java
        ), cat, "Tier 2")

        // Tier 3
        registerItems(listOf(
            Sword::class.java,
            Scimitar::class.java,
            Longsword::class.java
        ), cat, "Tier 3")

        // Tier 4
        registerItems(listOf(
            BattleAxe::class.java,
            Halberd::class.java,
            WarHammer::class.java
        ), cat, "Tier 4")

        // Tier 5
        registerItems(listOf(
            Glaive::class.java,
            Greataxe::class.java,
            Whip::class.java
        ), cat, "Tier 5")

        // Crafted
        registerItems(listOf(
            WoodenClub::class.java,
            StoneDagger::class.java,
            StoneAxe::class.java,
            IronSword::class.java,
            IronMace::class.java,
            DiamondBlade::class.java
        ), cat, "Crafted")

        // Missile
        registerItems(listOf(
            Dart::class.java,
            Shuriken::class.java,
            Boomerang::class.java,
            Javelin::class.java,
            Tamahawk::class.java,
            CurareDart::class.java,
            IncendiaryDart::class.java,
            ThrowingKnife::class.java,
            Bolas::class.java,
            ExplosiveBolt::class.java
        ), cat, "Missile")
    }

    // --- ARMOR ---

    private fun registerArmor() {
        val cat = EncyclopediaCategory.ARMOR

        registerItems(listOf(
            ClothArmor::class.java,
            LeatherArmor::class.java,
            MailArmor::class.java,
            ScaleArmor::class.java,
            PlateArmor::class.java
        ), cat, "Regular")

        registerItems(listOf(
            WarriorArmor::class.java,
            MageArmor::class.java,
            RogueArmor::class.java,
            HuntressArmor::class.java
        ), cat, "Class")

        registerItems(listOf(
            LeatherTunic::class.java,
            ChainVest::class.java,
            IronPlate::class.java,
            DiamondMail::class.java
        ), cat, "Crafted")
    }

    // --- POTIONS ---

    private fun registerPotions() {
        val cat = EncyclopediaCategory.POTIONS
        registerWithIcon("Potion of Healing", ItemSpriteSheet.POTION_TURQUOISE, PotionOfHealing::class.java, cat)
        registerWithIcon("Potion of Strength", ItemSpriteSheet.POTION_CRIMSON, PotionOfStrength::class.java, cat)
        registerWithIcon("Potion of Might", ItemSpriteSheet.POTION_AZURE, PotionOfMight::class.java, cat)
        registerWithIcon("Potion of Experience", ItemSpriteSheet.POTION_JADE, PotionOfExperience::class.java, cat)
        registerWithIcon("Potion of Speed", ItemSpriteSheet.POTION_GOLDEN, PotionOfSpeed::class.java, cat)
        registerWithIcon("Potion of Invisibility", ItemSpriteSheet.POTION_MAGENTA, PotionOfInvisibility::class.java, cat)
        registerWithIcon("Potion of Levitation", ItemSpriteSheet.POTION_CHARCOAL, PotionOfLevitation::class.java, cat)
        registerWithIcon("Potion of Mind Vision", ItemSpriteSheet.POTION_IVORY, PotionOfMindVision::class.java, cat)
        registerWithIcon("Potion of Frost", ItemSpriteSheet.POTION_AMBER, PotionOfFrost::class.java, cat)
        registerWithIcon("Potion of Liquid Flame", ItemSpriteSheet.POTION_BISTRE, PotionOfLiquidFlame::class.java, cat)
        registerWithIcon("Potion of Purity", ItemSpriteSheet.POTION_INDIGO, PotionOfPurity::class.java, cat)
        registerWithIcon("Potion of Shielding", ItemSpriteSheet.POTION_SILVER, PotionOfShielding::class.java, cat)
        registerWithIcon("Potion of Paralytic Gas", ItemSpriteSheet.POTION_ROSE, PotionOfParalyticGas::class.java, cat)
        registerWithIcon("Potion of Toxic Gas", ItemSpriteSheet.POTION_PEARL, PotionOfToxicGas::class.java, cat)
    }

    // --- SCROLLS ---

    private fun registerScrolls() {
        val cat = EncyclopediaCategory.SCROLLS
        registerWithIcon("Scroll of Identify", ItemSpriteSheet.SCROLL_KAUNAN, ScrollOfIdentify::class.java, cat)
        registerWithIcon("Scroll of Upgrade", ItemSpriteSheet.SCROLL_SOWILO, ScrollOfUpgrade::class.java, cat)
        registerWithIcon("Scroll of Remove Curse", ItemSpriteSheet.SCROLL_LAGUZ, ScrollOfRemoveCurse::class.java, cat)
        registerWithIcon("Scroll of Magic Mapping", ItemSpriteSheet.SCROLL_YNGVI, ScrollOfMagicMapping::class.java, cat)
        registerWithIcon("Scroll of Teleportation", ItemSpriteSheet.SCROLL_GYFU, ScrollOfTeleportation::class.java, cat)
        registerWithIcon("Scroll of Challenge", ItemSpriteSheet.SCROLL_RAIDO, ScrollOfChallenge::class.java, cat)
        registerWithIcon("Scroll of Enchantment", ItemSpriteSheet.SCROLL_ISAZ, ScrollOfEnchantment::class.java, cat)
        registerWithIcon("Scroll of Foresight", ItemSpriteSheet.SCROLL_MANNAZ, ScrollOfForesight::class.java, cat)
        registerWithIcon("Scroll of Lullaby", ItemSpriteSheet.SCROLL_NAUDIZ, ScrollOfLullaby::class.java, cat)
        registerWithIcon("Scroll of Mirror Image", ItemSpriteSheet.SCROLL_BERKANAN, ScrollOfMirrorImage::class.java, cat)
        registerWithIcon("Scroll of Psionic Blast", ItemSpriteSheet.SCROLL_ODAL, ScrollOfPsionicBlast::class.java, cat)
        registerWithIcon("Scroll of Recharging", ItemSpriteSheet.SCROLL_TIWAZ, ScrollOfRecharging::class.java, cat)
        registerWithIcon("Scroll of Terror", ItemSpriteSheet.SCROLL_ALGIZ, ScrollOfTerror::class.java, cat)
        registerWithIcon("Scroll of Transmutation", ItemSpriteSheet.SCROLL_THURISAZ, ScrollOfTransmutation::class.java, cat)
        registerWithIcon("Scroll of Wipe Out", ItemSpriteSheet.SCROLL_WIPE_OUT, ScrollOfWipeOut::class.java, cat)
    }

    // --- WANDS ---

    private fun registerWands() {
        val cat = EncyclopediaCategory.WANDS
        registerWithIcon("Wand of Magic Missile", ItemSpriteSheet.WAND_MAGIC_MISSILE, WandOfMagicMissile::class.java, cat)
        registerWithIcon("Wand of Firebolt", ItemSpriteSheet.WAND_HOLLY, WandOfFirebolt::class.java, cat)
        registerWithIcon("Wand of Lightning", ItemSpriteSheet.WAND_YEW, WandOfLightning::class.java, cat)
        registerWithIcon("Wand of Disintegration", ItemSpriteSheet.WAND_EBONY, WandOfDisintegration::class.java, cat)
        registerWithIcon("Wand of Avalanche", ItemSpriteSheet.WAND_CHERRY, WandOfAvalanche::class.java, cat)
        registerWithIcon("Wand of Poison", ItemSpriteSheet.WAND_TEAK, WandOfPoison::class.java, cat)
        registerWithIcon("Wand of Amok", ItemSpriteSheet.WAND_ROWAN, WandOfAmok::class.java, cat)
        registerWithIcon("Wand of Blink", ItemSpriteSheet.WAND_WILLOW, WandOfBlink::class.java, cat)
        registerWithIcon("Wand of Flock", ItemSpriteSheet.WAND_MAHOGANY, WandOfFlock::class.java, cat)
        registerWithIcon("Wand of Reach", ItemSpriteSheet.WAND_BAMBOO, WandOfReach::class.java, cat)
        registerWithIcon("Wand of Regrowth", ItemSpriteSheet.WAND_PURPLEHEART, WandOfRegrowth::class.java, cat)
        registerWithIcon("Wand of Slowness", ItemSpriteSheet.WAND_OAK, WandOfSlowness::class.java, cat)
        registerWithIcon("Wand of Teleportation", ItemSpriteSheet.WAND_BIRCH, WandOfTeleportation::class.java, cat)
    }

    // --- RINGS ---

    private fun registerRings() {
        val cat = EncyclopediaCategory.RINGS
        registerWithIcon("Ring of Accuracy", ItemSpriteSheet.RING_DIAMOND, RingOfAccuracy::class.java, cat)
        registerWithIcon("Ring of Detection", ItemSpriteSheet.RING_OPAL, RingOfDetection::class.java, cat)
        registerWithIcon("Ring of Elements", ItemSpriteSheet.RING_GARNET, RingOfElements::class.java, cat)
        registerWithIcon("Ring of Evasion", ItemSpriteSheet.RING_RUBY, RingOfEvasion::class.java, cat)
        registerWithIcon("Ring of Haggler", ItemSpriteSheet.RING_AMETHYST, RingOfHaggler::class.java, cat)
        registerWithIcon("Ring of Haste", ItemSpriteSheet.RING_TOPAZ, RingOfHaste::class.java, cat)
        registerWithIcon("Ring of Herbalism", ItemSpriteSheet.RING_ONYX, RingOfHerbalism::class.java, cat)
        registerWithIcon("Ring of Mending", ItemSpriteSheet.RING_TOURMALINE, RingOfMending::class.java, cat)
        registerWithIcon("Ring of Power", ItemSpriteSheet.RING_EMERALD, RingOfPower::class.java, cat)
        registerWithIcon("Ring of Satiety", ItemSpriteSheet.RING_SAPPHIRE, RingOfSatiety::class.java, cat)
        registerWithIcon("Ring of Shadows", ItemSpriteSheet.RING_QUARTZ, RingOfShadows::class.java, cat)
        registerWithIcon("Ring of Thorns", ItemSpriteSheet.RING_AGATE, RingOfThorns::class.java, cat)
    }

    // --- FOOD ---

    private fun registerFood() {
        val cat = EncyclopediaCategory.FOOD

        registerItems(listOf(
            Food::class.java,
            Pasty::class.java,
            MysteryMeat::class.java,
            OverpricedRation::class.java,
            StaleRation::class.java
        ), cat, "Basic")

        registerItems(listOf(
            ChargrilledMeat::class.java,
            FrozenCarpaccio::class.java,
            Apple::class.java,
            CheeseWedge::class.java,
            SmokeyBacon::class.java,
            HoneyBread::class.java,
            MushroomSoup::class.java,
            DragonPepper::class.java,
            FrostBerry::class.java,
            ElvenWaybread::class.java,
            PixieDustCake::class.java
        ), cat, "Special")

        registerItems(listOf(
            Wheat::class.java,
            Carrot::class.java,
            Potato::class.java,
            MelonSlice::class.java
        ), cat, "Farm Produce")

        registerItems(listOf(
            Bread::class.java,
            BakedPotato::class.java,
            RabbitStew::class.java
        ), cat, "Cooked")
    }

    // --- CRAFTING ---

    private fun registerCrafting() {
        val cat = EncyclopediaCategory.CRAFTING

        // Materials
        registerItems(listOf(
            Cobblestone::class.java,
            Fiber::class.java,
            Stick::class.java,
            WoodPlank::class.java,
            CobblestoneBlock::class.java,
            IronOre::class.java,
            IronIngot::class.java,
            GoldOre::class.java,
            GoldIngot::class.java,
            Leather::class.java,
            DiamondShard::class.java,
            ArcaneDust::class.java,
            BlankTome::class.java,
            EnchantedBook::class.java,
            EyeOfEnder::class.java,
            Bone::class.java,
            Bonemeal::class.java
        ), cat, "Materials")

        // Recipes from RecipeRegistry
        try {
            for (recipe in RecipeRegistry.all()) {
                val output = recipe.outputClass.getDeclaredConstructor().newInstance()
                val inputDesc = recipe.inputs.joinToString(", ") { input ->
                    val inputItem = try {
                        input.itemClass.getDeclaredConstructor().newInstance()
                    } catch (e: Exception) { null }
                    val inputName = inputItem?.name() ?: input.itemClass.simpleName ?: "?"
                    "${input.quantity}x $inputName"
                }
                val stationName = recipe.station.name
                entries.add(EncyclopediaEntry(
                    id = "recipe_${recipe.id}",
                    name = output.name(),
                    category = cat,
                    iconImage = output.image(),
                    iconType = IconType.ITEM,
                    description = "Craft: $inputDesc\nStation: $stationName" +
                            if (recipe.outputQuantity > 1) "\nYield: ${recipe.outputQuantity}" else "",
                    subcategory = "Recipes"
                ))
            }
        } catch (e: Exception) {
            // Skip if RecipeRegistry has issues
        }

        // Enchantments from EnchantmentRegistry
        try {
            for (entry in EnchantmentRegistry.all()) {
                entries.add(EncyclopediaEntry(
                    id = "enchant_${entry.displayName}",
                    name = entry.displayName,
                    category = cat,
                    iconImage = ItemSpriteSheet.ENCHANTED_BOOK,
                    iconType = IconType.ITEM,
                    description = "Tier ${entry.tier.ordinal + 1} enchantment.\n" +
                            "Dust cost: ${entry.tier.dustCost}",
                    subcategory = "Enchantments"
                ))
            }
        } catch (e: Exception) {
            // Skip if EnchantmentRegistry has issues
        }
    }

    // --- FARMING ---

    private fun registerFarming() {
        val cat = EncyclopediaCategory.FARMING

        // Crops from CropType enum
        for (crop in CropType.values()) {
            entries.add(EncyclopediaEntry(
                id = "crop_${crop.name}",
                name = crop.cropName,
                category = cat,
                iconImage = when (crop) {
                    CropType.WHEAT -> ItemSpriteSheet.WHEAT
                    CropType.CARROT -> ItemSpriteSheet.CARROT
                    CropType.POTATO -> ItemSpriteSheet.POTATO
                    CropType.MELON -> ItemSpriteSheet.MELON_SLICE
                },
                iconType = IconType.ITEM,
                description = "Growth time: ${crop.growthTime} turns\n" +
                        "Yield: ${crop.minYield}-${crop.maxYield}",
                stats = mapOf(
                    "Growth Time" to "${crop.growthTime} turns",
                    "Min Yield" to "${crop.minYield}",
                    "Max Yield" to "${crop.maxYield}"
                ),
                subcategory = "Crops"
            ))
        }

        // Seeds
        registerItems(listOf(
            com.watabou.pixeldungeon.farming.WheatSeed::class.java,
            com.watabou.pixeldungeon.farming.CarrotSeed::class.java,
            com.watabou.pixeldungeon.farming.PotatoSeed::class.java,
            com.watabou.pixeldungeon.farming.MelonSeed::class.java
        ), cat, "Seeds")

        // Tools
        registerItems(listOf(
            Hoe::class.java,
            WoodenBowl::class.java,
            WaterBucket::class.java,
            PlanterBox::class.java
        ), cat, "Tools")
    }

    // --- MONSTERS ---

    private fun registerMonsters() {
        registerMob(Rat::class.java, "Sewers", Assets.RAT)
        registerMob(Albino::class.java, "Sewers", Assets.RAT)
        registerMob(Gnoll::class.java, "Sewers", Assets.GNOLL)
        registerMob(Crab::class.java, "Sewers", Assets.CRAB)
        registerMob(Swarm::class.java, "Sewers", Assets.SWARM)

        registerMob(Skeleton::class.java, "Prison", Assets.SKELETON)
        registerMob(Thief::class.java, "Prison", Assets.THIEF)
        registerMob(Bandit::class.java, "Prison", Assets.THIEF)
        registerMob(Shaman::class.java, "Prison", Assets.SHAMAN)
        registerMob(Bat::class.java, "Prison", Assets.BAT)

        registerMob(Spinner::class.java, "Caves", Assets.SPINNER)
        registerMob(Brute::class.java, "Caves", Assets.BRUTE)
        registerMob(Shielded::class.java, "Caves", Assets.BRUTE)
        registerMob(Elemental::class.java, "Caves", Assets.ELEMENTAL)

        registerMob(Warlock::class.java, "Metropolis", Assets.WARLOCK)
        registerMob(Monk::class.java, "Metropolis", Assets.MONK)
        registerMob(Senior::class.java, "Metropolis", Assets.MONK)
        registerMob(Golem::class.java, "Metropolis", Assets.GOLEM)

        registerMob(Succubus::class.java, "Demon Halls", Assets.SUCCUBUS)
        registerMob(Eye::class.java, "Demon Halls", Assets.EYE)
        registerMob(Scorpio::class.java, "Demon Halls", Assets.SCORPIO)
        registerMob(Acidic::class.java, "Demon Halls", Assets.SCORPIO)

        registerMob(Goo::class.java, "Bosses", Assets.GOO)
        registerMob(Tengu::class.java, "Bosses", Assets.TENGU)
        registerMob(DM300::class.java, "Bosses", Assets.DM300)
        registerMob(King::class.java, "Bosses", Assets.KING)
        registerMob(Yog::class.java, "Bosses", Assets.YOG)

        registerMob(Wraith::class.java, "Special", Assets.WRAITH)
        registerMob(Piranha::class.java, "Special", Assets.PIRANHA)
        registerMob(Statue::class.java, "Special", Assets.STATUE)
        registerMob(Mimic::class.java, "Special", Assets.MIMIC)
    }

    private fun registerMob(cls: Class<out Mob>, subcategory: String, texture: String) {
        try {
            val mob = cls.getDeclaredConstructor().newInstance()
            entries.add(EncyclopediaEntry(
                id = cls.simpleName,
                name = mob.name,
                category = EncyclopediaCategory.MONSTERS,
                iconImage = 0,
                iconType = IconType.MOB,
                description = mob.description(),
                stats = mapOf(
                    "Health" to "${mob.maxHp()}",
                    "EXP" to "${mob.expValue()}",
                    "Defense" to "${mob.defenseSkillValue()}"
                ),
                subcategory = subcategory,
                spriteTexture = texture
            ))
        } catch (e: Exception) {
            // Skip mobs that can't be instantiated outside game
        }
    }

    // --- BUFFS ---

    private fun registerBuffs() {
        // Positive buffs
        registerBuff("Awareness", BuffIndicator.MIND_VISION, "Positive",
            "Reveals the position of all traps on the current level.")
        registerBuff("Barkskin", BuffIndicator.BARKSKIN, "Positive",
            "Adds extra armor from natural sources like Earthroot.")
        registerBuff("Combo", BuffIndicator.COMBO, "Positive",
            "Successive melee hits build combo damage. Each consecutive hit deals increasing bonus damage.")
        registerBuff("Fury", BuffIndicator.FURY, "Positive",
            "Below 50% health, deal 50% more damage. Berserker rage fuels your attacks.")
        registerBuff("Gas Immunity", BuffIndicator.IMMUNITY, "Positive",
            "Immune to all gaseous effects including toxic gas, paralytic gas, and confusion gas.")
        registerBuff("Invisibility", BuffIndicator.INVISIBLE, "Positive",
            "Enemies cannot see you. Attacking or using items breaks invisibility.")
        registerBuff("Levitation", BuffIndicator.LEVITATION, "Positive",
            "Float above the ground, avoiding traps and water effects.")
        registerBuff("Light", BuffIndicator.LIGHT, "Positive",
            "Increased field of view. See further in the dark dungeon.")
        registerBuff("Mind Vision", BuffIndicator.MIND_VISION, "Positive",
            "See all creatures on the current level, even through walls.")
        registerBuff("Regeneration", BuffIndicator.HEALING, "Positive",
            "Passively restores health over time. All heroes regenerate naturally.")
        registerBuff("Shadows", BuffIndicator.SHADOWS, "Positive",
            "Rogue stealth ability. Become harder to detect when standing still.")
        registerBuff("Sniper's Mark", BuffIndicator.MARK, "Positive",
            "Marked target takes extra damage from the sniper's ranged attacks.")
        registerBuff("Speed", BuffIndicator.LEVITATION, "Positive",
            "Move faster than normal. Covers ground quickly.")
        registerBuff("Well Fed", BuffIndicator.WELL_FED, "Positive",
            "Overfed state that provides bonus health regeneration.")

        // Negative buffs
        registerBuff("Amok", BuffIndicator.AMOK, "Negative",
            "Frenzied state. Attacks nearby creatures indiscriminately.")
        registerBuff("Bleeding", BuffIndicator.BLEEDING, "Negative",
            "Lose health each turn. Amount decreases over time. Moving makes it worse.")
        registerBuff("Blindness", BuffIndicator.BLINDNESS, "Negative",
            "Cannot see beyond adjacent tiles. Navigation becomes very difficult.")
        registerBuff("Burning", BuffIndicator.FIRE, "Negative",
            "Take fire damage each turn. Can ignite flammable terrain and destroy scrolls.")
        registerBuff("Charm", BuffIndicator.HEART, "Negative",
            "Charmed by an enemy. Cannot attack the charmer.")
        registerBuff("Cripple", BuffIndicator.CRIPPLE, "Negative",
            "Movement speed greatly reduced. Hobble slowly through the dungeon.")
        registerBuff("Frost", BuffIndicator.FROST, "Negative",
            "Frozen solid. Cannot act until the frost wears off. Shatters potions.")
        registerBuff("Hunger", BuffIndicator.HUNGER, "Negative",
            "You are hungry. Eat food to restore satiety. Starving causes damage over time.")
        registerBuff("Ooze", BuffIndicator.OOZE, "Negative",
            "Corrosive ooze deals damage each turn. Wash it off in water.")
        registerBuff("Paralysis", BuffIndicator.PARALYSIS, "Negative",
            "Cannot move or act. Completely immobilized for the duration.")
        registerBuff("Poison", BuffIndicator.POISON, "Negative",
            "Take damage each turn. Damage decreases as poison wears off.")
        registerBuff("Roots", BuffIndicator.ROOTS, "Negative",
            "Held in place by roots. Cannot move but can still attack and use items.")
        registerBuff("Sleep", BuffIndicator.PARALYSIS, "Negative",
            "Fast asleep. Any damage will wake you up.")
        registerBuff("Slow", BuffIndicator.SLOW, "Negative",
            "Actions take twice as long. Everything moves at half speed.")
        registerBuff("Terror", BuffIndicator.TERROR, "Negative",
            "Flee in terror from the source. Cannot attack while terrified.")
        registerBuff("Vertigo", BuffIndicator.VERTIGO, "Negative",
            "Disoriented. Movement direction becomes random and unpredictable.")
        registerBuff("Weakness", BuffIndicator.WEAKNESS, "Negative",
            "Reduced strength. Equipment may become too heavy to use effectively.")
    }

    private fun registerBuff(name: String, icon: Int, subcategory: String, description: String) {
        entries.add(EncyclopediaEntry(
            id = "buff_$name",
            name = name,
            category = EncyclopediaCategory.BUFFS,
            iconImage = icon,
            iconType = IconType.BUFF,
            description = description,
            subcategory = subcategory
        ))
    }

    // --- MECHANICS ---

    private fun registerMechanics() {
        val cat = EncyclopediaCategory.MECHANICS

        entries.add(EncyclopediaEntry(
            id = "mech_hunger",
            name = "Hunger",
            category = cat,
            iconImage = ItemSpriteSheet.RATION,
            iconType = IconType.ITEM,
            description = "Your hero gets hungry over time. When the hunger bar empties, you begin " +
                    "starving and take damage each turn. Eat food to restore satiety. Different foods " +
                    "restore different amounts of hunger. The Ration of Food is the most common food item, " +
                    "while Pasty restores the most hunger. Some special foods provide bonus effects."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_strength",
            name = "Strength",
            category = cat,
            iconImage = ItemSpriteSheet.POTION_CRIMSON,
            iconType = IconType.ITEM,
            description = "Strength determines which weapons and armor you can use effectively. " +
                    "Using equipment above your strength requirement incurs accuracy and speed penalties. " +
                    "Potions of Strength permanently increase your strength by 1. " +
                    "Upgrading equipment reduces its strength requirement."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_upgrades",
            name = "Upgrades",
            category = cat,
            iconImage = ItemSpriteSheet.SCROLL_KAUNAN,
            iconType = IconType.ITEM,
            description = "Scrolls of Upgrade improve weapons and armor. Each upgrade increases " +
                    "damage or defense, reduces strength requirement, and repairs durability. " +
                    "Upgraded items glow and show their upgrade level as +N. " +
                    "There is no limit to upgrades, but Scrolls of Upgrade are rare."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_enchanting",
            name = "Enchanting",
            category = cat,
            iconImage = ItemSpriteSheet.ARCANE_DUST,
            iconType = IconType.ITEM,
            description = "The Enchanting Table lets you apply enchantments to weapons using Arcane Dust. " +
                    "Enchantments are organized in 3 tiers of increasing power and cost. " +
                    "Tier 1: Sharpness, Knockback, Reach. " +
                    "Tier 2: Fire Aspect, Frost Aspect, Thunder Aspect. " +
                    "Tier 3: Vampirism, Soulbound, Sweeping Edge. " +
                    "Higher tiers require more Arcane Dust."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_leveling",
            name = "Leveling",
            category = cat,
            iconImage = ItemSpriteSheet.MASTERY,
            iconType = IconType.ITEM,
            description = "Defeating enemies grants experience points (EXP). When you accumulate enough " +
                    "EXP, you level up, gaining increased health, accuracy, and evasion. " +
                    "Enemies that are too far below your level stop granting EXP. " +
                    "At level 6, you can choose a subclass specialization using the Tome of Mastery."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_crafting",
            name = "Crafting System",
            category = cat,
            iconImage = ItemSpriteSheet.CRAFTING_KIT,
            iconType = IconType.ITEM,
            description = "Gather materials from the dungeon and craft equipment at stations. " +
                    "Basic crafting needs no station. The Crafting Table unlocks advanced recipes. " +
                    "The Furnace smelts ores into ingots and cooks food. " +
                    "The Enchanting Table applies enchantments. " +
                    "The Anvil repairs and modifies equipment."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_farming",
            name = "Farming System",
            category = cat,
            iconImage = ItemSpriteSheet.HOE,
            iconType = IconType.ITEM,
            description = "Till soil with a Hoe, plant seeds, and water with a Water Bucket to grow crops. " +
                    "Planter Boxes can be placed to create farmland anywhere. " +
                    "Crops take time to grow - check back after exploring more of the dungeon. " +
                    "Harvest crops for food ingredients, then cook them at a Furnace."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_durability",
            name = "Durability",
            category = cat,
            iconImage = ItemSpriteSheet.SHORT_SWORD,
            iconType = IconType.ITEM,
            description = "Weapons and armor degrade with use. When durability reaches zero, " +
                    "the item becomes broken and much less effective. Repair items by upgrading them " +
                    "with Scrolls of Upgrade, which fully restores durability. " +
                    "Higher-tier equipment degrades more slowly."
        ))

        entries.add(EncyclopediaEntry(
            id = "mech_identification",
            name = "Identification",
            category = cat,
            iconImage = ItemSpriteSheet.SCROLL_SOWILO,
            iconType = IconType.ITEM,
            description = "Potions, scrolls, wands, and rings start unidentified with randomized " +
                    "appearances each game. Use a Scroll of Identify to reveal an item's true nature. " +
                    "You can also identify items by using them, though this can be dangerous with cursed items. " +
                    "Once identified, all items of that type are recognized for the rest of the run."
        ))
    }
}
