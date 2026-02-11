package com.watabou.pixeldungeon.items.weapon
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.hero.HeroClass
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.KindOfWeapon
import com.watabou.pixeldungeon.items.weapon.enchantments.*
import com.watabou.pixeldungeon.items.weapon.missiles.MissileWeapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.ArrayList
import kotlin.math.pow
abstract class Weapon : KindOfWeapon() {
    var STR: Int = 10
    var ACU: Float = 1f
    var DLY: Float = 1f
    enum class Imbue {
        NONE, SPEED, ACCURACY
    }
    var imbue: Imbue = Imbue.NONE
    private var hitsToKnow: Int = HITS_TO_KNOW
    var enchantment: Enchantment? = null
    override fun proc(attacker: Char, defender: Char, damage: Int) {
        enchantment?.proc(this, attacker, defender, damage)
        if (!levelKnown) {
            hitsToKnow--
            if (hitsToKnow <= 0) {
                levelKnown = true
                GLog.i(TXT_IDENTIFY, name(), toString())
                Badges.validateItemLevelAquired(this)
            }
        }
        use()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(ENCHANTMENT, enchantment)
        bundle.put(IMBUE, imbue)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        hitsToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (hitsToKnow == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        enchantment = bundle[ENCHANTMENT] as Enchantment?
        imbue = bundle.getEnum(IMBUE, Imbue::class.java)
    }
    override fun acuracyFactor(hero: Hero): Float {
        var encumbrance = STR - hero.STR()
        if (this is MissileWeapon) {
            when (hero.heroClass) {
                HeroClass.WARRIOR -> encumbrance += 3
                HeroClass.HUNTRESS -> encumbrance -= 2
                else -> {}
            }
        }
        return (if (encumbrance > 0) (ACU / 1.5.pow(encumbrance.toDouble())).toFloat() else ACU) *
                (if (imbue == Imbue.ACCURACY) 1.5f else 1.0f)
    }
    override fun speedFactor(hero: Hero): Float {
        var encumrance = STR - hero.STR()
        if (this is MissileWeapon && hero.heroClass === HeroClass.HUNTRESS) {
            encumrance -= 2
        }
        return (if (encumrance > 0) (DLY * 1.2.pow(encumrance.toDouble())).toFloat() else DLY) *
                (if (imbue == Imbue.SPEED) 0.6f else 1.0f)
    }
    override fun damageRoll(owner: Hero): Int {
        var damage = super.damageRoll(owner)
        if ((owner.rangedWeapon != null) == (owner.heroClass === HeroClass.HUNTRESS)) {
            val exStr = owner.STR() - STR
            if (exStr > 0) {
                damage += Random.IntRange(0, exStr)
            }
        }
        return damage
    }
    open fun upgrade(enchant: Boolean): Item {
        if (enchantment != null) {
            if (!enchant && Random.Int(level()) > 0) {
                GLog.w(TXT_INCOMPATIBLE)
                enchant(null)
            }
        } else {
            if (enchant) {
                enchant()
            }
        }
        return super.upgrade()
    }
    override open fun upgrade(): Item {
        return upgrade(false)
    }
    override fun maxDurability(lvl: Int): Int {
        return 5 * if (lvl < 16) 16 - lvl else 1
    }
    override fun toString(): String {
        return if (levelKnown) Utils.format(
            if (isBroken) TXT_BROKEN else TXT_TO_STRING,
            super.toString(),
            STR
        ) else super.toString()
    }
    override fun name(): String {
        return enchantment?.name(super.name()) ?: super.name()
    }
    override fun random(): Item {
        if (Random.Float() < 0.4) {
            var n = 1
            if (Random.Int(3) == 0) {
                n++
                if (Random.Int(3) == 0) {
                    n++
                }
            }
            if (Random.Int(2) == 0) {
                upgrade(n)
            } else {
                degrade(n)
                cursed = true
            }
        }
        return this
    }
    fun enchant(ench: Enchantment?): Weapon {
        enchantment = ench
        return this
    }
    fun enchant(): Weapon {
        val oldEnchantment = enchantment?.let { it::class.java }
        var ench = Enchantment.random()
        while (ench != null && ench::class.java == oldEnchantment) {
            ench = Enchantment.random()
        }
        return enchant(ench)
    }
    val isEnchanted: Boolean
        get() = enchantment != null
    override fun glowing(): ItemSprite.Glowing? {
        return enchantment?.glowing()
    }
    abstract class Enchantment : Bundlable {
        abstract fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean
        open fun name(weaponName: String): String {
            return weaponName
        }
        override fun restoreFromBundle(bundle: Bundle) {
        }
        override fun storeInBundle(bundle: Bundle) {
        }
        open fun glowing(): ItemSprite.Glowing {
            return ItemSprite.Glowing.WHITE
        }
        companion object {
            private val enchants = arrayOf<Class<*>>(
                Fire::class.java, Poison::class.java, Death::class.java, Paralysis::class.java, Leech::class.java,
                Slow::class.java, Shock::class.java, Instability::class.java, Horror::class.java, Luck::class.java,
                Tempering::class.java, Vorpal::class.java, Chaining::class.java, Draining::class.java
            )
            private val chances = floatArrayOf(10f, 10f, 1f, 2f, 1f, 2f, 6f, 3f, 2f, 2f, 3f, 2f, 3f, 2f)
            fun random(): Enchantment? {
                try {
                    return enchants[Random.chances(chances)].getDeclaredConstructor().newInstance() as Enchantment
                } catch (e: Exception) {
                    return null
                }
            }
        }
    }
    companion object {
        private const val HITS_TO_KNOW = 20
        private const val TXT_IDENTIFY = "You are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_INCOMPATIBLE = "Interaction of different types of magic has negated the enchantment on this weapon!"
        private const val TXT_TO_STRING = "%s :%d"
        private const val TXT_BROKEN = "broken %s :%d"
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val ENCHANTMENT = "enchantment"
        private const val IMBUE = "imbue"
    }
}
