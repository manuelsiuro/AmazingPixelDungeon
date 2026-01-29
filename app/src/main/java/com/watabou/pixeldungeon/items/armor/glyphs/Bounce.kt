package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.mobs.Mob
import com.watabou.pixeldungeon.effects.Pushing
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.utils.Random
class Bounce : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 5) >= 4) {
            for (i in Level.NEIGHBOURS8.indices) {
                val ofs = Level.NEIGHBOURS8[i]
                if (attacker.pos - defender.pos == ofs) {
                    val newPos = attacker.pos + ofs
                    if ((Level.passable[newPos] || Level.avoid[newPos]) && Actor.findChar(newPos) == null) {
                        Actor.addDelayed(Pushing(attacker, attacker.pos, newPos), -1f)
                        attacker.pos = newPos
                        // FIXME
                        if (attacker is Mob) {
                            Dungeon.level?.mobPress(attacker)
                        } else {
                            Dungeon.level?.press(newPos, attacker)
                        }
                    }
                    break
                }
            }
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_BOUNCE, armorName)
    }
    companion object {
        private const val TXT_BOUNCE = "%s of bounce"
    }
}
