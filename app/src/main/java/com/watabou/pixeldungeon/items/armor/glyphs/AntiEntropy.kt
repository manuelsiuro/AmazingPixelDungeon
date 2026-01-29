package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.actors.buffs.Frost
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.effects.particles.SnowParticle
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class AntiEntropy : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 6) >= 5) {
            Buffs.prolong(attacker, Frost::class.java, Frost.duration(attacker) * Random.Float(1f, 1.5f))
            CellEmitter.get(attacker.pos).start(SnowParticle.FACTORY, 0.2f, 6)
            Buffs.affect(defender, Burning::class.java)?.reignite(defender)
            defender.sprite?.emitter()?.burst(FlameParticle.FACTORY, 5)
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_ANTI_ENTROPY, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return BLUE
    }
    companion object {
        private const val TXT_ANTI_ENTROPY = "%s of anti-entropy"
        private val BLUE = ItemSprite.Glowing(0x0000FF)
    }
}
