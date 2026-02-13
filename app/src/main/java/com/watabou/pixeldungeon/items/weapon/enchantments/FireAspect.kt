package com.watabou.pixeldungeon.items.weapon.enchantments

import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Burning
import com.watabou.pixeldungeon.effects.particles.FlameParticle
import com.watabou.pixeldungeon.items.weapon.Weapon
import com.watabou.pixeldungeon.sprites.ItemSprite

class FireAspect : Weapon.Enchantment() {

    override fun proc(weapon: Weapon, attacker: Char, defender: Char, damage: Int): Boolean {
        Buffs.affect(defender, Burning::class.java)?.reignite(defender)
        defender.sprite?.emitter()?.burst(FlameParticle.FACTORY, 3)
        return true
    }

    override fun glowing(): ItemSprite.Glowing {
        return ORANGE
    }

    override fun name(weaponName: String): String {
        return String.format(TXT_FIERY, weaponName)
    }

    companion object {
        private const val TXT_FIERY = "fiery %s"
        private val ORANGE = ItemSprite.Glowing(0xFF6600)
    }
}
