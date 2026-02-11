package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Bleeding
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.items.potions.PotionOfHealing
import com.watabou.pixeldungeon.sprites.AlbinoSprite
import com.watabou.utils.Random
class Albino : Rat() {
    init {
        name = "albino rat"
        spriteClass = AlbinoSprite::class.java
        HT = 15
        HP = HT

        loot = PotionOfHealing()
        lootChance = 0.25f
    }
    override fun die(src: Any?) {
        super.die(src)
        Badges.validateRare(this)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(2) == 0) {
            Buffs.affect(enemy, Bleeding::class.java)?.set(damage)
        }
        return damage
    }
}
