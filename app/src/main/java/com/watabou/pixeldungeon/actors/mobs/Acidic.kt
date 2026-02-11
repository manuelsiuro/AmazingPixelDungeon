package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.items.potions.PotionOfToxicGas
import com.watabou.pixeldungeon.sprites.AcidicSprite
import com.watabou.utils.Random
class Acidic : Scorpio() {
    init {
        name = "acidic scorpio"
        spriteClass = AcidicSprite::class.java

        loot = PotionOfToxicGas()
        lootChance = 0.3f
    }
    override fun defenseProc(enemy: Char, damage: Int): Int {
        val dmg = Random.IntRange(0, damage)
        if (dmg > 0) {
            enemy.damage(dmg, this)
        }
        return super.defenseProc(enemy, damage)
    }
    override fun die(src: Any?) {
        super.die(src)
        Badges.validateRare(this)
    }
}
