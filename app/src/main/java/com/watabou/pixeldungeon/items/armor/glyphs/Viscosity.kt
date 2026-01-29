package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.ResultDescriptions
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buff
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.pixeldungeon.utils.GLog
import com.watabou.pixeldungeon.utils.Utils
import com.watabou.utils.Bundle
import com.watabou.utils.Random
class Viscosity : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        if (damage == 0) {
            return 0
        }
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(level + 7) >= 6) {
            var debuff = defender.buff(DeferedDamage::class.java)
            if (debuff == null) {
                debuff = DeferedDamage()
                debuff.attachTo(defender)
            }
            debuff.prolong(damage)
            defender.sprite?.showStatus(CharSprite.WARNING, "deferred %d", damage)
            return 0
        } else {
            return damage
        }
    }
    override fun name(armorName: String): String {
        return String.format(TXT_VISCOSITY, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return PURPLE
    }
    class DeferedDamage : Buff() {
        protected var damage = 0
        override fun storeInBundle(bundle: Bundle) {
            super.storeInBundle(bundle)
            bundle.put(DAMAGE, damage)
        }
        override fun restoreFromBundle(bundle: Bundle) {
            super.restoreFromBundle(bundle)
            damage = bundle.getInt(DAMAGE)
        }
        override fun attachTo(target: Char): Boolean {
            if (super.attachTo(target)) {
                postpone(TICK)
                return true
            } else {
                return false
            }
        }
        fun prolong(damage: Int) {
            this.damage += damage
        }
        override fun icon(): Int {
            return BuffIndicator.DEFERRED
        }
        override fun toString(): String {
            return Utils.format("Defered damage (%d)", damage)
        }
        override fun act(): Boolean {
            val target = this.target ?: return false
            if (target.isAlive) {
                target.damage(1, this)
                if (target === Dungeon.hero && !target.isAlive) {
                    // FIXME
                    Dungeon.fail(Utils.format(ResultDescriptions.GLYPH, "enchantment of viscosity", Dungeon.depth))
                    GLog.n("The enchantment of viscosity killed you...")
                    Badges.validateDeathFromGlyph()
                }
                spend(TICK)
                if (--damage <= 0) {
                    detach()
                }
            } else {
                detach()
            }
            return true
        }
        companion object {
            private const val DAMAGE = "damage"
        }
    }
    companion object {
        private const val TXT_VISCOSITY = "%s of viscosity"
        private val PURPLE = ItemSprite.Glowing(0x8844CC)
    }
}
