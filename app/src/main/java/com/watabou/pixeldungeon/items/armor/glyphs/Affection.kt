package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Charm
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.GameMath
import com.watabou.utils.Random
class Affection : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = GameMath.gate(0f, armor.effectiveLevel().toFloat(), 6f).toInt()
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level / 2 + 5) >= 4) {
            var duration = Random.IntRange(3, 7).toFloat()
            val charmbuff = Buffs.affect(attacker, Charm::class.java, Charm.durationFactor(attacker) * duration)
            if (charmbuff != null) {
                charmbuff.`object` = defender.id()
            }
            attacker.sprite?.centerEmitter()?.start(Speck.factory(Speck.HEART), 0.2f, 5)
            duration *= Random.Float(0.5f, 1f)
            val charmbuff2 = Buffs.affect(defender, Charm::class.java, Charm.durationFactor(defender) * duration)
            if (charmbuff2 != null) {
                charmbuff2.`object` = attacker.id()
            }
            defender.sprite?.centerEmitter()?.start(Speck.factory(Speck.HEART), 0.2f, 5)
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_AFFECTION, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return PINK
    }
    companion object {
        private const val TXT_AFFECTION = "%s of affection"
        private val PINK = ItemSprite.Glowing(0xFF4488)
    }
}
