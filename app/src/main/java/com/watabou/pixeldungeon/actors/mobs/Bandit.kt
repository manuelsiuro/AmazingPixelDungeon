package com.watabou.pixeldungeon.actors.mobs
import com.watabou.pixeldungeon.Badges
import com.watabou.pixeldungeon.Dungeon
import com.watabou.pixeldungeon.actors.buffs.Blindness
import com.watabou.pixeldungeon.actors.buffs.Buffs
import com.watabou.pixeldungeon.actors.hero.Hero
import com.watabou.pixeldungeon.items.Gold
import com.watabou.pixeldungeon.sprites.BanditSprite
import com.watabou.utils.Random
class Bandit : Thief() {
    init {
        name = "crazy bandit"
        spriteClass = BanditSprite::class.java
    }

    override fun dropLoot() {
        if (Random.Float() < 0.8f) {
            Dungeon.level?.drop(Gold(Random.IntRange(50, 120)), pos)?.sprite?.drop()
        }
    }
    override fun steal(hero: Hero): Boolean {
        return if (super.steal(hero)) {
            Buffs.prolong(hero, Blindness::class.java, Random.Int(5, 12).toFloat())
            Dungeon.observe()
            true
        } else {
            false
        }
    }
    override fun die(src: Any?) {
        super.die(src)
        Badges.validateRare(this)
    }
}
