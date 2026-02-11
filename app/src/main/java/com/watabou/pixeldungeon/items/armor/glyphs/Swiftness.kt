package com.watabou.pixeldungeon.items.armor.glyphs

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Speed
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random

class Swiftness : Armor.Glyph() {

    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())

        // 25% + 5% per level chance to grant Speed buff
        if (Random.Int(100) < 25 + level * 5) {
            Buffs.prolong(defender, Speed::class.java, 1f)
        }

        return damage
    }

    override fun name(armorName: String): String {
        return String.format(TXT_SWIFTNESS, armorName)
    }

    override fun glowing(): ItemSprite.Glowing {
        return YELLOW
    }

    companion object {
        private const val TXT_SWIFTNESS = "swift %s"
        private val YELLOW = ItemSprite.Glowing(0xFFCC00)
    }
}
