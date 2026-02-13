package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.effects.particles.SnowParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite
import com.watabou.pixeldungeon.actors.buffs.Slow as SlowBuff

class FrostAspect : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        Buffs.prolong(defender, SlowBuff::class.java, 2f)
        defender.sprite?.emitter()?.burst(SnowParticle.FACTORY, 3)
        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return ICE_BLUE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_FROZEN, weaponName)
    }

    companion object {
        private const val TXT_FROZEN = "frozen %s"
        private val ICE_BLUE = ItemSprite.Glowing(0x4488FF)
    }
}
