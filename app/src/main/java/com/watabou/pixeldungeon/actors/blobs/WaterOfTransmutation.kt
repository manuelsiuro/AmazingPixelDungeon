package com.watabou.pixeldungeon.actors.blobs
import com.watabou.pixeldungeon.Journal
import com.watabou.pixeldungeon.Journal.Feature
import com.watabou.pixeldungeon.effects.BlobEmitter
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.potions.Potion
import com.watabou.pixeldungeon.items.potions.PotionOfMight
import com.watabou.pixeldungeon.items.potions.PotionOfStrength
import com.watabou.pixeldungeon.items.rings.Ring
import com.watabou.pixeldungeon.items.scrolls.Scroll
import com.watabou.pixeldungeon.items.scrolls.ScrollOfEnchantment
import com.watabou.pixeldungeon.items.scrolls.ScrollOfUpgrade
import com.watabou.pixeldungeon.items.wands.Wand
import com.watabou.pixeldungeon.items.weapon.melee.*
import com.watabou.pixeldungeon.plants.Plant
class WaterOfTransmutation : WellWater() {
    override fun affectItem(item: Item): Item? {
        var result: Item? = when (item) {
            is MeleeWeapon -> changeWeapon(item)
            is Scroll -> changeScroll(item)
            is Potion -> changePotion(item)
            is Ring -> changeRing(item)
            is Wand -> changeWand(item)
            is Plant.Seed -> changeSeed(item)
            else -> null
        }
        if (result != null) {
            Journal.remove(Feature.WELL_OF_TRANSMUTATION)
        }
        return result
    }
    override fun use(emitter: BlobEmitter) {
        super.use(emitter)
        emitter.start(Speck.factory(Speck.CHANGE), 0.2f, 0)
    }
    private fun changeWeapon(w: MeleeWeapon): MeleeWeapon? {
        val n: MeleeWeapon? = when (w) {
            is Knuckles -> Dagger()
            is Dagger -> Knuckles()
            is Spear -> Quarterstaff()
            is Quarterstaff -> Spear()
            is Sword -> Mace()
            is Mace -> Sword()
            is Longsword -> BattleAxe()
            is BattleAxe -> Longsword()
            is Glaive -> WarHammer()
            is WarHammer -> Glaive()
            else -> null
        }
        if (n != null) {
            val level = w.level()
            if (level > 0) {
                n.upgrade(level)
            } else if (level < 0) {
                n.degrade(-level)
            }
            if (w.isEnchanted) {
                n.enchant()
            }
            n.levelKnown = w.levelKnown
            n.cursedKnown = w.cursedKnown
            n.cursed = w.cursed
            Journal.remove(Feature.WELL_OF_TRANSMUTATION)
            return n
        } else {
            return null
        }
    }
    private fun changeRing(r: Ring): Ring {
        var n: Ring
        do {
            n = Generator.random(Generator.Category.RING) as Ring
        } while (n::class.java == r::class.java)
        n.level(0)
        val level = r.level()
        if (level > 0) {
            n.upgrade(level)
        } else if (level < 0) {
            n.degrade(-level)
        }
        n.levelKnown = r.levelKnown
        n.cursedKnown = r.cursedKnown
        n.cursed = r.cursed
        return n
    }
    private fun changeWand(w: Wand): Wand {
        var n: Wand
        do {
            n = Generator.random(Generator.Category.WAND) as Wand
        } while (n::class.java == w::class.java)
        n.level(0)
        n.upgrade(w.level())
        n.levelKnown = w.levelKnown
        n.cursedKnown = w.cursedKnown
        n.cursed = w.cursed
        return n
    }
    private fun changeSeed(s: Plant.Seed): Plant.Seed {
        var n: Plant.Seed
        do {
            n = Generator.random(Generator.Category.SEED) as Plant.Seed
        } while (n::class.java == s::class.java)
        return n
    }
    private fun changeScroll(s: Scroll): Scroll {
        return when (s) {
            is ScrollOfUpgrade -> ScrollOfEnchantment()
            is ScrollOfEnchantment -> ScrollOfUpgrade()
            else -> {
                var n: Scroll
                do {
                    n = Generator.random(Generator.Category.SCROLL) as Scroll
                } while (n::class.java == s::class.java)
                n
            }
        }
    }
    private fun changePotion(p: Potion): Potion {
        return when (p) {
            is PotionOfStrength -> PotionOfMight()
            is PotionOfMight -> PotionOfStrength()
            else -> {
                var n: Potion
                do {
                    n = Generator.random(Generator.Category.POTION) as Potion
                } while (n::class.java == p::class.java)
                n
            }
        }
    }
    override fun tileDesc(): String {
        return "Power of change radiates from the water of this well. " +
                "Throw an item into the well to turn it into something else."
    }
}
