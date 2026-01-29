package com.watabou.pixeldungeon.items.armor
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.EquipableItem
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.armor.glyphs.*
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundlable
import com.watabou.utils.Bundle
import com.watabou.utils.Random
import java.util.ArrayList
abstract class Armor(val tier: Int) : EquipableItem() {
    var STR: Int = 0
    private var hitsToKnow: Int = HITS_TO_KNOW
    var glyph: Glyph? = null
    init {
        STR = typicalSTR()
    }
    override fun actions(hero: Hero): ArrayList<String> {
        val actions = super.actions(hero)
        actions.add(if (isEquipped(hero)) AC_UNEQUIP else AC_EQUIP)
        return actions
    }
    override fun isEquipped(hero: Hero): Boolean {
        return hero.belongings.armor === this
    }
    override fun doEquip(hero: Hero): Boolean {
        detachAll(hero.belongings.backpack)
        if (hero.belongings.armor == null || hero.belongings.armor?.doUnequip(hero, true) == true) {
            hero.belongings.armor = this
            updateQuickslot()
            cursedKnown = true
            if (cursed) {
                equipCursed(hero)
                GLog.n(TXT_EQUIP_CURSED, name())
            }
            hero.spendAndNext(time2equip(hero))
            return true
        } else {
            collect(hero.belongings.backpack)
            return false
        }
    }
    override fun time2equip(hero: Hero): Float {
        return TIME_TO_EQUIP
    }
    override fun doUnequip(hero: Hero, collect: Boolean, single: Boolean): Boolean {
        if (super.doUnequip(hero, collect, single)) {
            hero.belongings.armor = null
            return true
        } else {
            return false
        }
    }
    open fun DR(): Int {
        return (tier * if (isBroken) 0 else level() + 1)
    }
    override fun upgrade(): Item {
        return upgrade(false)
    }
    fun upgrade(inscribe: Boolean): Item {
        if (glyph != null) {
            if (!inscribe && Random.Int(level()) > 0) {
                GLog.w(TXT_INCOMPATIBLE)
                inscribe(null)
            }
        } else {
            if (inscribe) {
                inscribe()
            }
        }
        STR--
        return super.upgrade()
    }
    fun safeUpgrade(): Item {
        return upgrade(glyph != null)
    }
    override fun degrade(): Item {
        STR++
        return super.degrade()
    }
    override fun maxDurability(lvl: Int): Int {
        return 6 * if (lvl < 16) 16 - lvl else 1
    }
    open fun proc(attacker: Char, defender: Char, damage: Int): Int {
        glyph?.let { g ->
            return g.proc(this, attacker, defender, damage)
        }
        if (!levelKnown) {
        	hitsToKnow--
            if (hitsToKnow <= 0) {
                levelKnown = true
                GLog.i(TXT_IDENTIFY, name(), toString())
                Badges.validateItemLevelAquired(this)
            }
        }
        use()
        return damage
    }
    override fun toString(): String {
        return if (levelKnown) Utils.format(
            if (isBroken) TXT_BROKEN else TXT_TO_STRING,
            super.toString(),
            glyph?.name() ?: "armor"
        ) else super.toString()
    }
    override fun name(): String {
        return glyph?.name(super.name()) ?: super.name()
    }
    override fun info(): String {
        val p = "\n\n"
        val info = StringBuilder(desc())
        val lvl = visiblyUpgraded()
        val quality = if (lvl != 0) {
            if (lvl > 0) {
                if (isBroken) "broken" else "upgraded"
            } else {
                "degraded"
            }
        } else {
            ""
        }
        info.append(p)
        info.append("This $name is ${Utils.indefinite(quality)}")
        info.append(" tier-$tier armor.")
        if (isIdentified) {
            info.append(" It provides damage absorption up to ${DR()} points per attack.")
        } else {
            info.append(" Its typical damage absorption is up to ${typicalDR()} points per attack")
            info.append(" and usually it requires ${typicalSTR()} points of strength.")
            val hero = Dungeon.hero
            if (hero != null && typicalSTR() > hero.STR()) {
                info.append(" Probably this armor is too heavy for you.")
            }
        }
        if (glyph != null) {
            info.append(" It is inscribed.")
        }
        val hero = Dungeon.hero
        if (hero != null && isIdentified && hero.belongings.backpack.items.contains(this)) {
            if (STR > hero.STR()) {
                info.append(p)
                info.append(
                    "Because of your inadequate strength this armor will absorb less damage " +
                            "and your movement speed will be decreased."
                )
            }
        }
        val equippedHero = Dungeon.hero
        if (equippedHero != null && isEquipped(equippedHero)) {
            info.append(p)
            val armorName = name()
            info.append(
                "You are wearing the $armorName${if (cursed) ", and because it is cursed, you are powerless to take it off" else ""}."
            )
        } else {
            if (cursedKnown && cursed) {
                info.append(p)
                info.append("You can feel a malevolent magic lurking within $name.")
            }
        }
        return info.toString()
    }
    override fun storeInBundle(bundle: Bundle) {
        super.storeInBundle(bundle)
        bundle.put(UNFAMILIRIARITY, hitsToKnow)
        bundle.put(GLYPH, glyph)
    }
    override fun restoreFromBundle(bundle: Bundle) {
        super.restoreFromBundle(bundle)
        hitsToKnow = bundle.getInt(UNFAMILIRIARITY)
        if (hitsToKnow == 0) {
            hitsToKnow = HITS_TO_KNOW
        }
        glyph = bundle[GLYPH] as Glyph?
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
    fun typicalSTR(): Int {
        return 8 + tier * 2
    }
    fun typicalDR(): Int {
        return tier
    }
    override fun price(): Int {
        var price = 20 * (1 shl (tier - 1))
        if (glyph != null) {
            price = (price * 1.5).toInt()
        }
        return considerState(price)
    }
    fun inscribe(glyph: Glyph?): Armor {
        this.glyph = glyph
        return this
    }
    fun inscribe(): Armor {
        val oldGlyph = glyph?.let { it::class.java }
        var gl = Glyph.random()
        while (gl != null && gl::class.java == oldGlyph) {
            gl = Glyph.random()
        }
        return inscribe(gl)
    }
    val isInscribed: Boolean
        get() = glyph != null
    override fun glowing(): ItemSprite.Glowing? {
        return glyph?.glowing()
    }
    abstract class Glyph : Bundlable {
        abstract fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int
        open fun name(armorName: String): String {
        	return armorName
        }
        open fun name(): String {
            return "glyph"
        }
        override fun restoreFromBundle(bundle: Bundle) {
        }
        override fun storeInBundle(bundle: Bundle) {
        }
        open fun glowing(): ItemSprite.Glowing {
            return ItemSprite.Glowing.WHITE
        }
        fun checkOwner(owner: Char) {
            // Need to find the armor that this glyph is attached to.
            // Since Glyph is attached to an Armor, we can iterate over the owner's belongings if they are a Hero.
            if (owner is Hero) {
                val armor = owner.belongings.armor
                if (armor != null && armor.glyph === this && !armor.isIdentified) {
                    val ub = usageBuff
                    val mark = if (ub != null) owner.buff(ub) else null
                    if (mark != null) {
                         GLog.w(TXT_IDENTIFY, armor.name(), armor.toString())
                         Badges.validateItemLevelAquired(armor)
                         armor.levelKnown = true
                    }
                }
            }
        }
        companion object {
            // To be set by a specific glyph
            var usageBuff: Class<out com.watabou.pixeldungeon.actors.buffs.Buff>? = null
            private val glyphs = arrayOf<Class<*>>(
                Bounce::class.java, Affection::class.java, AntiEntropy::class.java, Multiplicity::class.java,
                Potential::class.java, Metabolism::class.java, Stench::class.java, Viscosity::class.java,
                Displacement::class.java, Entanglement::class.java, AutoRepair::class.java
            )
            private val chances = floatArrayOf(1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f)
            fun random(): Glyph? {
                try {
                    return glyphs[Random.chances(chances)].getDeclaredConstructor().newInstance() as Glyph
                } catch (e: Exception) {
                    return null
                }
            }
        }
    }
    companion object {
        private const val HITS_TO_KNOW = 10
        private const val TXT_EQUIP_CURSED = "your %s constricts around you painfully"
        private const val TIME_TO_EQUIP = 1f
        private const val TXT_IDENTIFY = "You are now familiar enough with your %s to identify it. It is %s."
        private const val TXT_INCOMPATIBLE = "Interaction of different types of magic has negated the glyph on this armor!"
        private const val TXT_TO_STRING = "%s :%s"
        private const val TXT_BROKEN = "broken %s :%s"
        private const val UNFAMILIRIARITY = "unfamiliarity"
        private const val GLYPH = "glyph"
    }
}
