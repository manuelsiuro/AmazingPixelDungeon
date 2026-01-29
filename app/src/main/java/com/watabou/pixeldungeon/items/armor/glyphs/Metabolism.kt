package com.watabou.pixeldungeon.items.armor.glyphs
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Hunger
import com.watabou.pixeldungeon.effects.Speck
import com.watabou.pixeldungeon.items.armor.Armor
import com.watabou.pixeldungeon.sprites.CharSprite
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.ui.BuffIndicator
import com.watabou.utils.Random
class Metabolism : Armor.Glyph() {
    override fun proc(armor: Armor, attacker: Char, defender: Char, damage: Int): Int {
        val level = Math.max(0, armor.effectiveLevel())
        if (Random.Int(level / 2 + 5) >= 4) {
            val healing = Math.min(defender.HT - defender.HP, Random.Int(1, defender.HT / 5))
            if (healing > 0) {
                val hunger = defender.buff(Hunger::class.java)
                if (hunger != null && !hunger.isStarving) {
                    hunger.satisfy(-Hunger.STARVING / 10f)
                    BuffIndicator.refreshHero()
                    defender.HP += healing
                    defender.sprite?.emitter()?.burst(Speck.factory(Speck.HEALING), 1)
                    defender.sprite?.showStatus(CharSprite.POSITIVE, Integer.toString(healing))
                }
            }
        }
        return damage
    }
    override fun name(armorName: String): String {
        return String.format(TXT_METABOLISM, armorName)
    }
    override fun glowing(): ItemSprite.Glowing {
        return RED
    }
    companion object {
        private const val TXT_METABOLISM = "%s of metabolism"
        private val RED = ItemSprite.Glowing(0xCC0000)
    }
}
