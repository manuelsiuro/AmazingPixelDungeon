package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.ItemSprite
class AutoRepair : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        if (defender is Hero && Dungeon.gold >= armor.tier) {
            Dungeon.gold -= armor.tier
            armor.polish()
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_AUTO_REPAIR, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return GRAY
    }
    companion object {
        private const val TXT_AUTO_REPAIR = "%s of auto-repair"
        private val GRAY = ItemSprite.Glowing(0xCC8888)
    }
}
