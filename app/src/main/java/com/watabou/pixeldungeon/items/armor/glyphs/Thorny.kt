package com.watabou.pixeldungeon.items.armor.glyphs

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite

class Thorny : Armor.Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        // Reflect damage only to melee (adjacent) attackers
        if (Level.adjacent(attacker.pos, defender.pos)) {
            val reflected = 1 + Math.max(0, armor.effectiveLevel())
            attacker.damage(reflected, this)
        }

        return damage
    }

    override fun name(armorName: String): String {
        return String.format(TXT_THORNY, armorName)
    }

    override fun glowing(): ItemSprite.Glowing {
        return GREEN
    }

    companion object {
        private const val TXT_THORNY = "thorny %s"
        private val GREEN = ItemSprite.Glowing(0x006600)
    }
}
