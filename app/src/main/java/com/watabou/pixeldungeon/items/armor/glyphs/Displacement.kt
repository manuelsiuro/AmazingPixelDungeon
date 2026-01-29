package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Displacement : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        if (Dungeon.bossLevel()) {
            return damage
        }
        val level = armor.effectiveLevel()
        val nTries = (if (level < 0) 1 else level + 1) * 5
        for (i in 0 until nTries) {
            val pos = Random.Int(Level.LENGTH)
            if (Dungeon.visible[pos] && Level.passable[pos] && Actor.findChar(pos) == null) {
                WandOfBlink.appear(defender, pos)
                Dungeon.level?.press(pos, defender)
                Dungeon.observe()
                break
            }
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_DISPLACEMENT, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return BLUE
    }
    companion object {
        private const val TXT_DISPLACEMENT = "%s of displacement"
        private val BLUE = ItemSprite.Glowing(0x66AAFF)
    }
}
