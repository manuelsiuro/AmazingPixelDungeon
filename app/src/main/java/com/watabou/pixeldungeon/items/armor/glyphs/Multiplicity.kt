package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.actors.Actor
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.actors.mobs.npcs.MirrorImage
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.items.wands.WandOfBlink
import com.watabou.pixeldungeon.levels.Level
import com.watabou.pixeldungeon.scenes.GameScene
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.utils.Random
import java.util.ArrayList
class Multiplicity : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(level / 2 + 6) >= 5) {
            val respawnPoints = ArrayList<Int>()
            for (i in Level.NEIGHBOURS8.indices) {
                val p = defender.pos + Level.NEIGHBOURS8[i]
                if (Actor.findChar(p) == null && (Level.passable[p] || Level.avoid[p])) {
                    respawnPoints.add(p)
                }
            }
            if (respawnPoints.size > 0) {
                val mob = MirrorImage()
                mob.duplicate(defender as Hero)
                GameScene.add(mob)
                WandOfBlink.appear(mob, Random.element(respawnPoints) ?: 0)
                defender.damage(Random.IntRange(1, defender.HT / 6), this)
                checkOwner(defender)
            }
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_MULTIPLICITY, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return PINK
    }
    companion object {
        private const val TXT_MULTIPLICITY = "%s of multiplicity"
        private val PINK = ItemSprite.Glowing(0xCCAA88)
    }
}
