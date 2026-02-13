package com.watabou.pixeldungeon.items
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.armor.*
import com.watabou.pixeldungeon.items.bags.Bag
import com.watabou.pixeldungeon.items.food.Apple
import com.watabou.pixeldungeon.items.food.ElvenWaybread
import com.watabou.pixeldungeon.items.food.Food
import com.watabou.pixeldungeon.items.food.HoneyBread
import com.watabou.pixeldungeon.items.food.MushroomSoup
import com.watabou.pixeldungeon.items.food.MysteryMeat
import com.watabou.pixeldungeon.items.food.Pasty
import com.watabou.pixeldungeon.items.food.PixieDustCake
import com.watabou.pixeldungeon.items.food.SmokeyBacon
import com.watabou.pixeldungeon.items.food.StaleRation
import com.watabou.pixeldungeon.items.potions.*
import com.watabou.pixeldungeon.items.rings.*
import com.watabou.pixeldungeon.items.scrolls.*
import com.watabou.pixeldungeon.items.wands.*
import com.watabou.pixeldungeon.items.crafting.*
import com.watabou.pixeldungeon.items.weapon.*
import com.watabou.pixeldungeon.items.weapon.melee.*
import com.watabou.pixeldungeon.items.weapon.missiles.*
import com.watabou.pixeldungeon.farming.*
import com.watabou.pixeldungeon.plants.*
import com.watabou.utils.Random
import java.util.HashMap
object Generator {
    enum class Category(
        val prob: Float,
        val superClass: Class<out Item>
    ) {
        WEAPON(15f, Weapon::class.java),
        ARMOR(10f, Armor::class.java),
        POTION(50f, Potion::class.java),
        SCROLL(40f, Scroll::class.java),
        WAND(4f, Wand::class.java),
        RING(2f, Ring::class.java),
        SEED(5f, Plant.Seed::class.java),
        FOOD(0f, Food::class.java),
        GOLD(50f, Gold::class.java),
        MISC(5f, Item::class.java),
        MATERIAL(0f, MaterialItem::class.java);
        var classes: Array<Class<*>>? = null
        var probs: FloatArray? = null
        companion object {
            fun order(item: Item): Int {
                for (i in values().indices) {
                    if (values()[i].superClass.isInstance(item)) {
                        return i
                    }
                }
                return if (item is Bag) Int.MAX_VALUE else Int.MAX_VALUE - 1
            }
        }
    }
    private val categoryProbs = HashMap<Category, Float>()
    init {
        Category.GOLD.classes = arrayOf(Gold::class.java)
        Category.GOLD.probs = floatArrayOf(1f)
        Category.SCROLL.classes = arrayOf(
            ScrollOfIdentify::class.java,
            ScrollOfTeleportation::class.java,
            ScrollOfRemoveCurse::class.java,
            ScrollOfRecharging::class.java,
            ScrollOfMagicMapping::class.java,
            ScrollOfChallenge::class.java,
            ScrollOfTerror::class.java,
            ScrollOfLullaby::class.java,
            ScrollOfPsionicBlast::class.java,
            ScrollOfMirrorImage::class.java,
            ScrollOfUpgrade::class.java,
            ScrollOfEnchantment::class.java,
            ScrollOfTransmutation::class.java,
            ScrollOfForesight::class.java
        )
        Category.SCROLL.probs = floatArrayOf(30f, 10f, 15f, 10f, 15f, 12f, 8f, 8f, 4f, 6f, 0f, 1f, 4f, 8f)
        Category.POTION.classes = arrayOf(
            PotionOfHealing::class.java,
            PotionOfExperience::class.java,
            PotionOfToxicGas::class.java,
            PotionOfParalyticGas::class.java,
            PotionOfLiquidFlame::class.java,
            PotionOfLevitation::class.java,
            PotionOfStrength::class.java,
            PotionOfMindVision::class.java,
            PotionOfPurity::class.java,
            PotionOfInvisibility::class.java,
            PotionOfMight::class.java,
            PotionOfFrost::class.java,
            PotionOfSpeed::class.java,
            PotionOfShielding::class.java
        )
        Category.POTION.probs = floatArrayOf(45f, 4f, 15f, 10f, 15f, 10f, 0f, 20f, 12f, 10f, 0f, 10f, 8f, 10f)
        Category.WAND.classes = arrayOf(
            WandOfTeleportation::class.java,
            WandOfSlowness::class.java,
            WandOfFirebolt::class.java,
            WandOfRegrowth::class.java,
            WandOfPoison::class.java,
            WandOfBlink::class.java,
            WandOfLightning::class.java,
            WandOfAmok::class.java,
            WandOfReach::class.java,
            WandOfFlock::class.java,
            WandOfMagicMissile::class.java,
            WandOfDisintegration::class.java,
            WandOfAvalanche::class.java
        )
        Category.WAND.probs = floatArrayOf(10f, 10f, 15f, 6f, 10f, 11f, 15f, 10f, 6f, 10f, 0f, 5f, 5f)
        Category.WEAPON.classes = arrayOf(
            Dagger::class.java,
            Knuckles::class.java,
            Quarterstaff::class.java,
            Spear::class.java,
            Mace::class.java,
            Sword::class.java,
            Longsword::class.java,
            BattleAxe::class.java,
            WarHammer::class.java,
            Glaive::class.java,
            ShortSword::class.java,
            Dart::class.java,
            Javelin::class.java,
            IncendiaryDart::class.java,
            CurareDart::class.java,
            Shuriken::class.java,
            Boomerang::class.java,
            Tamahawk::class.java,
            Whip::class.java,
            Scimitar::class.java,
            Halberd::class.java,
            Greataxe::class.java,
            ThrowingKnife::class.java,
            Bolas::class.java,
            ExplosiveBolt::class.java
        )
        Category.WEAPON.probs = floatArrayOf(
            1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f, 1f, 1f, 1f, 1f, 0f, 1f,
            1f, 1f, 1f, 1f, 1f, 1f, 1f
        )
        Category.ARMOR.classes = arrayOf(
            ClothArmor::class.java,
            LeatherArmor::class.java,
            MailArmor::class.java,
            ScaleArmor::class.java,
            PlateArmor::class.java
        )
        Category.ARMOR.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f)
        Category.FOOD.classes = arrayOf(
            Food::class.java,
            Pasty::class.java,
            MysteryMeat::class.java,
            StaleRation::class.java,
            Apple::class.java,
            SmokeyBacon::class.java,
            MushroomSoup::class.java,
            HoneyBread::class.java,
            ElvenWaybread::class.java,
            PixieDustCake::class.java
        )
        Category.FOOD.probs = floatArrayOf(4f, 1f, 0f, 3f, 2f, 1f, 1f, 0.5f, 0.2f, 0.3f)
        Category.RING.classes = arrayOf(
            RingOfMending::class.java,
            RingOfDetection::class.java,
            RingOfShadows::class.java,
            RingOfPower::class.java,
            RingOfHerbalism::class.java,
            RingOfAccuracy::class.java,
            RingOfEvasion::class.java,
            RingOfSatiety::class.java,
            RingOfHaste::class.java,
            RingOfElements::class.java,
            RingOfHaggler::class.java,
            RingOfThorns::class.java
        )
        Category.RING.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 0f)
        Category.SEED.classes = arrayOf(
            Firebloom.Seed::class.java,
            Icecap.Seed::class.java,
            Sorrowmoss.Seed::class.java,
            Dreamweed.Seed::class.java,
            Sungrass.Seed::class.java,
            Earthroot.Seed::class.java,
            Fadeleaf.Seed::class.java,
            Rotberry.Seed::class.java,
            Thornvine.Seed::class.java,
            Mistbloom.Seed::class.java,
            Brightcap.Seed::class.java,
            Venomroot.Seed::class.java,
            WheatSeed::class.java,
            CarrotSeed::class.java,
            PotatoSeed::class.java,
            MelonSeed::class.java
        )
        Category.SEED.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 0f, 1f, 1f, 1f, 1f, 2f, 1.5f, 1f, 0.5f)
        Category.MISC.classes = arrayOf(
            Bomb::class.java,
            Honeypot::class.java,
            SmokeBomb::class.java,
            HolyWater::class.java,
            ThrowingNet::class.java
        )
        Category.MISC.probs = floatArrayOf(2f, 1f, 1.5f, 1f, 1.5f)
        Category.MATERIAL.classes = arrayOf(
            Cobblestone::class.java,
            Fiber::class.java,
            Stick::class.java,
            WoodPlank::class.java,
            IronOre::class.java,
            IronIngot::class.java,
            GoldOre::class.java,
            GoldIngot::class.java,
            Leather::class.java,
            DiamondShard::class.java
        )
        Category.MATERIAL.probs = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
    }
    fun reset() {
        for (cat in Category.values()) {
            categoryProbs[cat] = cat.prob
        }
    }
    fun random(): Item? {
        val cat = Random.chances(categoryProbs) ?: return null
        return random(cat)
    }
    fun random(cat: Category): Item? {
        try {
            val currentProb = categoryProbs[cat] ?: return null
            categoryProbs[cat] = currentProb / 2
            return when (cat) {
                Category.ARMOR -> randomArmor()
                Category.WEAPON -> randomWeapon()
                else -> {
                    val classes = cat.classes ?: return null
                    val probs = cat.probs ?: return null
                    @Suppress("UNCHECKED_CAST")
                    (classes[Random.chances(probs)] as Class<out Item>).getDeclaredConstructor().newInstance().random()
                }
            }
        } catch (e: Exception) {
            return null
        }
    }
    fun random(cl: Class<out Item>): Item? {
        return try {
            cl.getDeclaredConstructor().newInstance().random()
        } catch (e: Exception) {
            null
        }
    }
    @Throws(Exception::class)
    fun randomArmor(): Armor {
        val curStr = Hero.STARTING_STR + Dungeon.potionOfStrength
        val cat = Category.ARMOR
        val classes = cat.classes ?: throw Exception("Armor classes not initialized")
        val probs = cat.probs ?: throw Exception("Armor probs not initialized")
        val a1 = classes[Random.chances(probs)].getDeclaredConstructor().newInstance() as Armor
        val a2 = classes[Random.chances(probs)].getDeclaredConstructor().newInstance() as Armor
        a1.random()
        a2.random()
        return if (Math.abs(curStr - a1.STR) < Math.abs(curStr - a2.STR)) a1 else a2
    }
    @Throws(Exception::class)
    fun randomWeapon(): Weapon {
        val curStr = Hero.STARTING_STR + Dungeon.potionOfStrength
        val cat = Category.WEAPON
        val classes = cat.classes ?: throw Exception("Weapon classes not initialized")
        val probs = cat.probs ?: throw Exception("Weapon probs not initialized")
        val w1 = classes[Random.chances(probs)].getDeclaredConstructor().newInstance() as Weapon
        val w2 = classes[Random.chances(probs)].getDeclaredConstructor().newInstance() as Weapon
        w1.random()
        w2.random()
        return if (Math.abs(curStr - w1.STR) < Math.abs(curStr - w2.STR)) w1 else w2
    }
}
