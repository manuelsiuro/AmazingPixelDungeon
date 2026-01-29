package com.watabou.pixeldungeon.items.weapon.melee
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.items.Item
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Random
open class MeleeWeapon(private val tier: Int, acu: Float, dly: Float) : Weapon() {
    init {
        ACU = acu
        DLY = dly
        STR = typicalSTR()
    }
    protected open fun min0(): Int {
        return tier
    }
    protected open fun max0(): Int {
        return ((tier * tier - tier + 10) / ACU * DLY).toInt()
    }
    override fun min(): Int {
        return if (isBroken) min0() else min0() + level()
    }
    override fun max(): Int {
        return if (isBroken) max0() else max0() + level() * tier
    }
    override fun upgrade(): Item {
        return upgrade(false)
    }
    override fun upgrade(enchant: Boolean): Item {
        STR--
        return super.upgrade(enchant)
    }
    fun safeUpgrade(): Item {
        return upgrade(enchantment != null)
    }
    override fun degrade(): Item {
        STR++
        return super.degrade()
    }
    fun typicalSTR(): Int {
        return 8 + tier * 2
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
        info.append(" tier-$tier melee weapon. ")
        if (levelKnown) {
            val min = min()
            val max = max()
            info.append("Its average damage is ${min + (max - min) / 2} points per hit. ")
        } else {
            val min = min0()
            val max = max0()
            info.append(
                "Its typical average damage is ${min + (max - min) / 2} points per hit " +
                        "and usually it requires ${typicalSTR()} points of strength. "
            )
            if (typicalSTR() > Dungeon.hero?.STR() ?: 0) {
                info.append("Probably this weapon is too heavy for you. ")
            }
        }
        if (DLY != 1f) {
            info.append("This is a rather ${if (DLY < 1f) "fast" else "slow"}")
            if (ACU != 1f) {
                if ((ACU > 1f) == (DLY < 1f)) {
                    info.append(" and ")
                } else {
                    info.append(" but ")
                }
                info.append(if (ACU > 1f) "accurate" else "inaccurate")
            }
            info.append(" weapon. ")
        } else if (ACU != 1f) {
            info.append("This is a rather ${if (ACU > 1f) "accurate" else "inaccurate"} weapon. ")
        }
        when (imbue) {
            Imbue.SPEED -> info.append("It was balanced to make it faster. ")
            Imbue.ACCURACY -> info.append("It was balanced to make it more accurate. ")
            Imbue.NONE -> {
            }
        }
        if (enchantment != null) {
            info.append("It is enchanted.")
        }
        if (levelKnown && Dungeon.hero?.belongings?.backpack?.items?.contains(this) == true) {
            if (STR > Dungeon.hero?.STR() ?: 0) {
                info.append(p)
                info.append(
                    "Because of your inadequate strength the accuracy and speed " +
                            "of your attack with this $name is decreased."
                )
            }
            if (STR < Dungeon.hero?.STR() ?: 0) {
                info.append(p)
                info.append(
                    "Because of your excess strength the damage " +
                            "of your attack with this $name is increased."
                )
            }
        }
        val hero = Dungeon.hero
        if (hero != null && isEquipped(hero)) {
            info.append(p)
            info.append(
                "You hold the $name at the ready${if (cursed) ", and because it is cursed, you are powerless to let go." else "."}"
            )
        } else {
            if (cursedKnown && cursed) {
                info.append(p)
                info.append("You can feel a malevolent magic lurking within $name.")
            }
        }
        return info.toString()
    }
    override fun price(): Int {
        var price = 20 * (1 shl (tier - 1))
        if (enchantment != null) {
            price = (price * 1.5).toInt()
        }
        return considerState(price)
    }
    override fun random(): Item {
        super.random()
        if (Random.Int(10 + level()) == 0) {
            enchant()
        }
        return this
    }
}
