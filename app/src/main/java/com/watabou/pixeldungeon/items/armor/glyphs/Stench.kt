package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.blobs.Blob
import com.watabou.pixeldungeon.actors.blobs.ToxicGas
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
class Stench : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Level.adjacent(attacker.pos, defender.pos) && Random.Int(level + 5) >= 4) {
            Blob.seed(attacker.pos, 20, ToxicGas::class.java)?.let { GameScene.add(it) }
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_STENCH, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return GREEN
    }
    companion object {
        private const val TXT_STENCH = "%s of stench"
        private val GREEN = ItemSprite.Glowing(0x22CC44)
    }
}
