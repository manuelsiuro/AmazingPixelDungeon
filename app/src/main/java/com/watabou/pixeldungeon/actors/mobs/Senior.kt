package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.actors.Char
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.buffs.Paralysis
import com.watabou.pixeldungeon.items.Generator
import com.watabou.pixeldungeon.sprites.SeniorSprite
import com.watabou.utils.Random
class Senior : Monk() {
    init {
        name = "senior monk"
        spriteClass = SeniorSprite::class.java

        loot = Generator.Category.WAND
        lootChance = 0.1f
    }
    override fun damageRoll(): Int {
        return Random.NormalIntRange(12, 20)
    }
    override fun attackProc(enemy: Char, damage: Int): Int {
        if (Random.Int(10) == 0) {
            Buffs.prolong(enemy, Paralysis::class.java, 1.1f)
        }
        return super.attackProc(enemy, damage)
    }
    override fun die(src: Any?) {
        super.die(src)
        Badges.validateRare(this)
    }
}
