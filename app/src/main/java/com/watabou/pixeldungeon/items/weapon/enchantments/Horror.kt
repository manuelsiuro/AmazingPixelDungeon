package com.watabou.pixeldungeon.items.weapon.enchantments
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Terror
import com.watabou.pixeldungeon.actors.buffs.Vertigo
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Horror : Weapon.Enchantment() {
    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        // lvl 0 - 20%
        // lvl 1 - 33%
        // lvl 2 - 43%
        val level = Math.max(0, weapon.effectiveLevel())
        if (Random.Int(level + 5) >= 4) {
            if (defender === Dungeon.hero) {
                Buffs.affect(defender, Vertigo::class.java, Vertigo.duration(defender))
            } else {
                val terror = Buffs.affect(defender, Terror::class.java, Terror.DURATION)
                if (terror != null) {
                    terror.`object` = attacker.id()
                }
            }
            return true
        } else {
            return false
        }
    }
    override fun glowing(): ItemSprite.Glowing {
        return GREY
    }
    override fun name(weaponName: String): String {
        return String.format(TXT_ELDRITCH, weaponName)
    }
    companion object {
        private const val TXT_ELDRITCH = "eldritch %s"
        private val GREY = ItemSprite.Glowing(0x222222)
    }
}
