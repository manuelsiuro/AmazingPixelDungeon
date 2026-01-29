package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.noosa.Camera
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Roots
import com.watabou.pixeldungeon.effects.CellEmitter
import com.watabou.pixeldungeon.effects.particles.EarthParticle
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.plants.Earthroot
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Entanglement : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(4) == 0) {
            Buffs.prolong(defender, Roots::class.java, (5 - level / 5).toFloat())
            Buffs.affect(defender, Earthroot.Armor::class.java)?.level(5 * (level + 1))
            CellEmitter.bottom(defender.pos).start(EarthParticle.FACTORY, 0.05f, 8)
            Camera.main?.shake(1f, 0.4f)
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_ENTANGLEMENT, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return GREEN
    }
    companion object {
        private const val TXT_ENTANGLEMENT = "%s of entanglement"
        private val GREEN = ItemSprite.Glowing(0x448822)
    }
}
