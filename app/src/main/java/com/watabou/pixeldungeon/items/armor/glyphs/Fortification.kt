package com.watabou.pixeldungeon.items.armor.glyphs

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Fortification : Armor.Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())

        // 30% + 5% per level chance to reduce damage by 25%
        return if (Random.Int(100) < 30 + level * 5) {
            damage * 3 / 4
        } else {
            damage
        }
    }

    override fun name(armorName: String): String {
        return String.format(TXT_FORTIFICATION, armorName)
    }

    override fun glowing(): ItemSprite.Glowing {
        return GREY
    }

    companion object {
        private const val TXT_FORTIFICATION = "%s of fortification"
        private val GREY = ItemSprite.Glowing(0x999999)
    }
}
