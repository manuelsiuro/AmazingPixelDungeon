package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.effects.Lightning
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.levels.traps.LightningTrap
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Potential : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 7) >= 6) {
            var dmg = Random.IntRange(1, damage)
            attacker.damage(dmg, LightningTrap.LIGHTNING)
            dmg = Random.IntRange(1, dmg)
            defender.damage(dmg, LightningTrap.LIGHTNING)
            checkOwner(defender)
            if (defender === Dungeon.hero) {
                Camera.main?.shake(2f, 0.3f)
            }
            val points = intArrayOf(attacker.pos, defender.pos)
            attacker.sprite?.parent?.add(Lightning(points, 2, null))
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_POTENTIAL, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return BLUE
    }
    companion object {
        private const val TXT_POTENTIAL = "%s of potential"
        private val BLUE = ItemSprite.Glowing(0x66CCEE)
    }
}
